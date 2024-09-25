from flask import Flask, request, jsonify
from ocr_processing import process_image
import os

app = Flask(__name__)

BASE_PATH = "C:/Users/404ST011/Documents/GitHub/illiterate/src/main/resources"

@app.route('/ocr', methods=['POST'])
def ocr_api():
    data = request.get_json()
    if 'image_path' not in data:
        return jsonify({"error": "No image path provided"}), 400
    
    image_path = os.path.join(BASE_PATH, data['image_path'])
    
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
