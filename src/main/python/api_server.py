from flask import Flask, request, jsonify
from ocr_processing import process_image
import os

app = Flask(__name__)

BASE_PATH = "C:/Users/user/Documents/Github/illiterate/src/main/resources"

@app.route('/ocr', methods=['POST'])
def ocr_api():
    data = request.get_json()
    relative_image_path = data['image_path']
    full_image_path = os.path.normpath(os.path.join(BASE_PATH, relative_image_path.lstrip('/')))
    full_image_path = full_image_path.replace('\\', '/')

    print(f"Relative image path: {relative_image_path}")
    print(f"Full image path: {full_image_path}")
    print(f"File exists: {os.path.exists(full_image_path)}")
    print(f"File size: {os.path.getsize(full_image_path) if os.path.exists(full_image_path) else 'N/A'}")

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
