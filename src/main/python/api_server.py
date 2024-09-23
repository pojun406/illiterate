from flask import Flask, request, jsonify
from ocr_processing import process_image
import os

app = Flask(__name__)

@app.route('/ocr', methods=['POST'])
def ocr_api():
    data = request.get_json()
    if 'image_path' not in data:
        return jsonify({"error": "No image path provided"}), 400
    image_path = data['image_path']
    if not os.path.exists(image_path):
        return jsonify({"error": "Image path does not exist"}), 400
    try:
        result = process_image(image_path)
        return jsonify(result)
    except Exception as e:
        # 예외 발생 시, 자세한 오류 메시지 출력
        print(f"Error occurred: {str(e)}")
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
