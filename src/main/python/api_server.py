from flask import Flask, request, jsonify
from ocr_processing import process_image
import os

app = Flask(__name__)

@app.route('/ocr', methods=['POST'])
def ocr_api():
    if 'file' not in request.files:
        return jsonify({"error": "No file part"}), 400
    file = request.files['file']
    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400
    if file:
        filename = file.filename
        file_path = os.path.join('temp', filename)
        file.save(file_path)
        try:
            result = process_image(file_path)
            return jsonify(result)
        except Exception as e:
            return jsonify({"error": str(e)}), 500
        finally:
            os.remove(file_path)

if __name__ == '__main__':
    os.makedirs('temp', exist_ok=True)
    app.run(debug=True, host='0.0.0.0', port=5000)
