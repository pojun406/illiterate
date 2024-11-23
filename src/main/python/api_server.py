from flask import Flask, request, Response, jsonify
from ocr_processing import process_image
from paper_info_processing import select_rois_with_descriptions
import logging
from pathlib import Path
import os
import multiprocessing
import json
import cv2
import base64

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger(__name__)


app = Flask(__name__)

@app.route('/ocr', methods=['POST'])
def ocr_api():
    data = request.get_json()
    if not data:
        return jsonify({"error": "No data provided"}), 400

    # Java에서 전달된 경로 가져오기
    full_image_path = data.get('image_path')
    if not full_image_path:
        return jsonify({"error": "Image path not provided"}), 400

    try:
        # 경로 정규화
        full_image_path = Path(full_image_path).resolve(strict=False).as_posix()

        # 파일 존재 여부 확인
        if not os.path.isfile(full_image_path):
            return jsonify({"error": f"Image file does not exist: {full_image_path}"}), 400
        if not os.access(full_image_path, os.R_OK):
            return jsonify({"error": f"No read permission for file: {full_image_path}"}), 400

        # OCR 프로세싱 호출
        logger.info(f"Starting OCR processing for image: {full_image_path}")
        result = process_image(full_image_path)

        # 결과를 반환
        return jsonify(result)

    except FileNotFoundError as e:
        logger.error(f"File not found: {str(e)}")
        return jsonify({"error": f"File not found: {str(e)}"}), 400
    except Exception as e:
        logger.error(f"Unexpected error: {str(e)}", exc_info=True)
        return jsonify({"error": f"Unexpected error: {str(e)}"}), 500

def crop_and_encode_base64(image_path, roi_data, key="제목"):
    """
    특정 키에 해당하는 ROI를 기준으로 이미지를 자르고 Base64로 인코딩하여 반환.

    Args:
        image_path (str): 원본 이미지 경로.
        roi_data (dict): ROI 데이터.
        key (str): ROI에서 찾을 키. 기본값은 '제목'.

    Returns:
        str: 잘라낸 이미지를 Base64로 인코딩한 문자열.
    """
    image = cv2.imread(image_path)
    if image is None:
        raise ValueError(f"Image not found or cannot be loaded: {image_path}")

    # 특정 키에 해당하는 벡터값 찾기
    roi_key = next((k for k, v in roi_data.items() if v == key), None)
    if roi_key is None:
        raise ValueError(f"'{key}' key not found in ROI data")

    # 벡터값 파싱
    coordinates = eval(roi_key)
    x1, y1 = coordinates[0]
    x2, y2 = coordinates[3]

    # 이미지 자르기
    cropped_image = image[y1:y2, x1:x2]
    if cropped_image.size == 0:
        raise ValueError(f"Cropped image is empty. Coordinates: {coordinates}")

    # 이미지를 JPEG로 인코딩 후 Base64로 변환
    _, buffer = cv2.imencode('.jpg', cropped_image)
    encoded_image = base64.b64encode(buffer).decode("utf-8")

    return encoded_image


def run_gui_process(image_path, output_file, window_width, window_height):
    """
    GUI 프로세스 실행 함수
    """
    try:
        select_rois_with_descriptions(
            image_path=image_path,
            output_file=output_file,
            window_width=window_width,
            window_height=window_height
        )
    except Exception as e:
        print(f"Error in GUI process: {e}")


@app.route('/paperinfo', methods=['POST'])
def send_rois():
    data = request.get_json()
    image_path = data.get('image_path')

    if not image_path:
        return jsonify({"error": "Image path not provided"}), 400

    if not os.path.isfile(image_path):
        return jsonify({"error": f"Image file does not exist: {image_path}"}), 400

    try:
        # `paper_info_processing`에서 ROI 데이터를 가져오도록 변경
        roi_data = select_rois_with_descriptions(image_path)

        # "제목"에 해당하는 이미지를 잘라 Base64로 인코딩
        title_img = None
        possible_keys = ["제목", "title", "Title"]
        for key in possible_keys:
            try:
                title_img = crop_and_encode_base64(image_path, roi_data, key=key)
                if title_img:  # "제목"이 성공적으로 처리되면 반복 종료
                    break
            except ValueError as ve:
                logger.warning(f"Error processing key '{key}': {ve}")

        if not title_img:
            logger.error("Unable to process '제목' or 'title' key from ROI data.")
            return jsonify({"error": "Unable to process '제목' or 'title' key from ROI data."}), 400

        response_data = {
            "roi_data": roi_data,
            "title_img": title_img  # Base64로 인코딩된 잘린 이미지
        }

        return jsonify(response_data), 200
    except Exception as e:
        error_message = f"Error processing ROI data: {str(e)}"
        logger.error(error_message, exc_info=True)
        return jsonify({"error": error_message}), 500


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)
