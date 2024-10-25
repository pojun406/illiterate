from flask import Flask, request, Response, jsonify
from ocr_processing import process_image
import os
import json

app = Flask(__name__)

@app.route('/ocr', methods=['POST'])
def ocr_api():
    data = request.get_json()

    if not data:
        return jsonify({"error": "No data provided"}), 400

    # Spring에서 전달된 경로를 가져옴
    full_image_path = data.get('image_path')
    if not full_image_path:
        return jsonify({"error": "Image path not provided"}), 400

    # 경로에 Windows 드라이브 레터가 포함된 경우 제거
    if ':' in full_image_path:
        _, full_image_path = full_image_path.split(':', 1)  # "D:/app/image"에서 "D:" 제거
        full_image_path = os.path.join('/', full_image_path.lstrip('/'))  # "/app/image" 형식으로 변환

    full_image_path = os.path.normpath(full_image_path).replace('\\', '/')  # 정규화 후 Windows 경로 -> Linux 경로로 변경

    if not os.path.isfile(full_image_path):
        return jsonify({"error": f"Image file does not exist: {full_image_path}"}), 400

    if not os.access(full_image_path, os.R_OK):
        return jsonify({"error": f"No read permission for file: {full_image_path}"}), 400

    try:
        result = process_image(full_image_path)
        response = Response(result, content_type='application/json; charset=utf-8')
        with open('ocr_result.json', 'w', encoding='utf-8') as json_file:
            json.dump(json.loads(result), json_file, ensure_ascii=False, indent=4)

        return response
    except Exception as e:
        error_message = f"Error occurred: {str(e)}"
        print(error_message)
        return jsonify({"error": error_message}), 500

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
