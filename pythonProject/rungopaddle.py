from letsgopaddle import MyPaddleOCR
import os

def main():
    ocr = MyPaddleOCR()

    # 사용자로부터 이미지 파일 경로 입력 받기
    base_directory = 'C:/Users/404ST011/PycharmProjects/pythonProject/photo'
    image_filename = input("이미지 파일 이름을 입력하세요: ")
    image_path = os.path.join(base_directory, image_filename)

    if not os.path.exists(image_path):
        print(f"파일을 찾을 수 없습니다: {image_path}")
        return

    # OCR 실행 및 디버그 모드 활성화
    result = ocr.run_ocr(image_path, debug=True)
    print("OCR 결과:")
    for item in result:
        print(item)

if __name__ == '__main__':
    main()