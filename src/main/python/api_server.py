from flask import Flask, request, jsonify
from ocr_processing import process_image
import os

app = Flask(__name__)

@app.route('/ocr', methods=['POST'])
def ocr_api():
    data = request.get_json()
    if 'image_path' not in data:
        return jsonify({"error": "No image path provided"}), 400
    
    # 상대 경로로 변경
    relative_path = data['image_path']
    base_path = os.path.join(os.path.dirname(__file__), '..', '..', 'resources')
    image_path = os.path.join(base_path, relative_path)
    
    if not os.path.isfile(image_path):
        return jsonify({"error": f"Image file does not exist: {image_path}"}), 400
    if not os.access(image_path, os.R_OK):
        return jsonify({"error": f"No read permission for file: {image_path}"}), 400
    
    try:
        result = process_image(image_path)
        return jsonify(result)
    except Exception as e:
        error_message = f"Error occurred: {str(e)}"
        print(error_message)
        return jsonify({"error": error_message}), 500

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
