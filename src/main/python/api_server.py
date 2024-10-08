from flask import Flask, request, jsonify
from ocr_processing import process_image
import os

app = Flask(__name__)

# 서버에서 이미지 파일을 찾기 위한 기본 경로 설정
BASE_PATH = os.getenv('IMAGE_PATH', '/app/image')

@app.route('/ocr', methods=['POST'])
def ocr_api():
    data = request.get_json()

    if not data:
        return jsonify({"error": "No data provided"}), 400

    relative_image_path = data.get('image_path')
    if not relative_image_path:
        return jsonify({"error": "Image path not provided"}), 400

    # relative_image_path가 BASE_PATH를 이미 포함하고 있는지 확인
    if relative_image_path.startswith(BASE_PATH):
        full_image_path = os.path.normpath(relative_image_path)
    else:
        full_image_path = os.path.normpath(os.path.join(BASE_PATH, relative_image_path.lstrip('/')))

    full_image_path = full_image_path.replace('\\', '/')  # Windows 경로를 Linux 스타일로 변경

    if not os.path.isfile(full_image_path):
        return jsonify({"error": f"Image file does not exist: {full_image_path}"}), 400

    if not os.access(full_image_path, os.R_OK):
        return jsonify({"error": f"No read permission for file: {full_image_path}"}), 400

    try:
        result = process_image(full_image_path)
        return jsonify(result)
    except Exception as e:
        error_message = f"Error occurred: {str(e)}"
        print(error_message)
        return jsonify({"error": error_message}), 500

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
