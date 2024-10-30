from flask import Flask, request, Response, jsonify
from ocr_processing import process_image
from paper_info_processing import select_roi  # paper_info_processing.py에서 import
import os
import json

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
            json.dump(result, json_file, ensure_ascii=False, indent=4)
            
        return response
    except Exception as e:
        error_message = f"Error occurred: {str(e)}"
        print(error_message)
        return jsonify({"error": error_message}), 500

@app.route('/paperinfo', methods=['POST'])
def paper_info_api():
    data = request.get_json()
    if not data:
        return jsonify({"error": "No data provided"}), 400

    image_path = data.get('image_path')
    json_name = data.get('json_name', 'roi_data.json')
    if not image_path:
        return jsonify({"error": "Image path not provided"}), 400

    if ':' in image_path:
        _, image_path = image_path.split(':', 1)
        image_path = os.path.join('/', image_path.lstrip('/'))

    image_path = os.path.normpath(image_path).replace('\\', '/')
    if not os.path.isfile(image_path):
        return jsonify({"error": f"Image file does not exist: {image_path}"}), 400
    if not os.access(image_path, os.R_OK):
        return jsonify({"error": f"No read permission for file: {image_path}"}), 400

    try:
        title_vectors = select_roi(image_path, json_name)
        return jsonify({"title_vectors": title_vectors, "message": "ROI data saved successfully."})
    except Exception as e:
        error_message = f"Error occurred: {str(e)}"
        print(error_message)
        return jsonify({"error": error_message}), 500

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
