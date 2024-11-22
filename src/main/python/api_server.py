from flask import Flask, request, Response, jsonify
from ocr_processing import process_image
from paper_info_processing import select_rois_with_descriptions
import os
import multiprocessing
import json
import cv2
import base64

app = Flask(__name__)

@app.route('/ocr', methods=['POST'])
def ocr_api():
    data = request.get_json()
    if not data:
        return jsonify({"error": "No data provided"}), 400

    full_image_path = data.get('image_path')
    if not full_image_path:
        return jsonify({"error": "Image path not provided"}), 400

    if ':' in full_image_path:
        _, full_image_path = full_image_path.split(':', 1)
        full_image_path = os.path.join('/', full_image_path.lstrip('/'))

    full_image_path = os.path.normpath(full_image_path).replace('\\', '/')
    if not os.path.isfile(full_image_path):
        return jsonify({"error": f"Image file does not exist: {full_image_path}"}), 400
    if not os.access(full_image_path, os.R_OK):
        return jsonify({"error": f"No read permission for file: {full_image_path}"}), 400

    try:
        result = process_image(full_image_path)
        # JSON 직렬화를 위해 jsonify로 감싸서 반환
        response = jsonify(result)

        # JSON 파일로 저장
        with open('ocr_result.json', 'w', encoding='utf-8') as json_file:
            json.dump(json.loads(result), json_file, ensure_ascii=False, indent=4)
            
        return response
    except Exception as e:
        error_message = f"Error occurred: {str(e)}"
        print(error_message)
        return jsonify({"error": error_message}), 500

def crop_image(image_path, coordinates):
    """이미지를 주어진 좌표대로 자르고 Base64로 인코딩하여 반환"""
    image = cv2.imread(image_path)
    if image is None:
        raise ValueError("Image not found or unable to load.")

    x1, y1, x2, y2 = coordinates
    cropped_image = image[y1:y2, x1:x2]
    _, buffer = cv2.imencode('.jpg', cropped_image)
    encoded_image = base64.b64encode(buffer).decode("utf-8")
    return encoded_image

def run_gui_process(image_path, output_file):
    """
    GUI 프로세스 실행 함수
    """
    try:
        select_rois_with_descriptions(image_path, output_file)
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

    # GUI를 비동기로 실행
    gui_process = multiprocessing.Process(target=run_gui_process, args=(image_path, output_file), daemon=True)
    gui_process.start()

    # 타임아웃 설정
    gui_process.join(timeout=300)  # 300초(5분) 후 종료 대기
    if gui_process.is_alive():
        gui_process.terminate()  # 타임아웃 초과 시 강제 종료
        return jsonify({"message": "GUI process terminated after timeout"}), 500

    # 즉시 응답 반환
    return jsonify({"message": "GUI process started successfully"}), 200

if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)
