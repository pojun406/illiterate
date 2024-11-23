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

def crop_image(image_path, coordinates):
    """
    이미지를 주어진 좌표대로 자르고 Base64로 인코딩하여 반환
    """
    image = cv2.imread(image_path)
    if image is None:
        raise ValueError("Image not found or unable to load.")

    x1, y1, x2, y2 = coordinates
    cropped_image = image[y1:y2, x1:x2]
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
    output_file = "roi_descriptions.json"

    if not image_path:
        return jsonify({"error": "Image path not provided"}), 400

    if not os.path.isfile(image_path):
        return jsonify({"error": f"Image file does not exist: {image_path}"}), 400

    # 창 크기 기본값 설정
    window_width = 1200
    window_height = 800

    # GUI를 비동기로 실행
    gui_process = multiprocessing.Process(
        target=run_gui_process,
        args=(image_path, output_file, window_width, window_height),
        daemon=True
    )
    gui_process.start()
    gui_process.join(timeout=300)  # 300초(5분) 후 종료 대기

    if gui_process.is_alive():
        gui_process.terminate()  # 타임아웃 초과 시 강제 종료
        return jsonify({"message": "GUI process terminated after timeout"}), 500

    try:
        # ROI 데이터 로드
        with open(output_file, "r", encoding="utf-8") as f:
            roi_data = json.load(f)

        # title_img 생성
        title_img = None
        title_key = next((k for k, v in roi_data.items() if v in ['title', '제목']), None)
        if title_key:
            coordinates = eval(title_key)
            title_img = crop_image(image_path, (coordinates[0][0], coordinates[0][1], coordinates[1][0], coordinates[1][1]))

        response_data = {
            "roi_data": roi_data,
            "title_img": title_img  # Base64로 인코딩된 잘린 이미지
        }

        return jsonify(response_data), 200
    except Exception as e:
        error_message = f"Error processing ROI data: {str(e)}"
        print(error_message)
        return jsonify({"error": error_message}), 500


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)
