from flask import Flask, request, jsonify  # Flask 웹 프레임워크 및 JSON 응답을 위한 모듈
from ocr_processing import process_image  # OCR 처리 함수 불러오기
import os  # 파일 경로 및 시스템 관련 작업을 위한 모듈

# Flask 애플리케이션 초기화
app = Flask(__name__)

# 서버에서 이미지 파일을 찾기 위한 기본 경로 설정
BASE_PATH = "D:/Project/illiterate/src/main/resources"

# '/ocr' 경로에 POST 요청을 처리하는 API 엔드포인트 생성
@app.route('/ocr', methods=['POST'])
def ocr_api():
    # 클라이언트로부터 JSON 데이터를 받아옴
    data = request.get_json()

    # 클라이언트가 전송한 이미지 경로를 받아서 절대 경로로 변환
    relative_image_path = data['image_path']  # 상대 경로를 가져옴
    full_image_path = os.path.normpath(os.path.join(BASE_PATH, relative_image_path.lstrip('/')))  # 절대 경로로 변환
    full_image_path = full_image_path.replace('\\', '/')  # Windows 경로를 Linux 스타일로 변경 (일관성 유지)

    # 경로 및 파일 관련 정보를 출력 (디버깅 목적)
    print(f"Relative image path: {relative_image_path}")  # 클라이언트가 보낸 경로 출력
    print(f"Full image path: {full_image_path}")  # 변환된 절대 경로 출력
    print(f"File exists: {os.path.exists(full_image_path)}")  # 파일 존재 여부 확인
    print(f"File size: {os.path.getsize(full_image_path) if os.path.exists(full_image_path) else 'N/A'}")  # 파일 크기 출력

    # 이미지 파일이 존재하지 않으면 오류 반환
    if not os.path.isfile(full_image_path):
        return jsonify({"error": f"Image file does not exist: {full_image_path}"}), 400

    # 파일 읽기 권한이 없을 경우 오류 반환
    if not os.access(full_image_path, os.R_OK):
        return jsonify({"error": f"No read permission for file: {full_image_path}"}), 400

    try:
        # 이미지 처리 및 OCR 실행
        result = process_image(full_image_path)
        return jsonify(result)  # 처리된 결과를 JSON 형태로 반환
    except Exception as e:
        # 처리 중 오류가 발생한 경우 오류 메시지 출력 및 반환
        error_message = f"Error occurred: {str(e)}"
        print(error_message)
        return jsonify({"error": error_message}), 500  # 서버 오류 상태 코드와 함께 반환

# 서버 실행: 디버그 모드로 실행하며, 모든 IP 주소에서 접근 가능하게 설정
if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
