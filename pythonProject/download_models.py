from letsgopaddle import MyPaddleOCR
import os

def download_ocr_models():
    # 더미 이미지 파일 경로 설정
    dummy_image_path = 'photo/11.png'

    # 더미 이미지 파일 생성
    if not os.path.exists(dummy_image_path):
        # 100x100 크기의 검은색 더미 이미지 생성
        from PIL import Image
        image = Image.new('RGB', (100, 100), (0, 0, 0))
        image.save(dummy_image_path)

    # MyPaddleOCR 인스턴스 생성 (모델 파일 다운로드)
    ocr = MyPaddleOCR()

    print("모델 파일 다운로드가 완료되었습니다.")

    # 더미 이미지 파일 삭제
    os.remove(dummy_image_path)

if __name__ == '__main__':
    download_ocr_models()
