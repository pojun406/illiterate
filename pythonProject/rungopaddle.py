from letsgopaddle import MyPaddleOCR
import sys
import os

def main(image_path):
    ocr = MyPaddleOCR()

    if not os.path.exists(image_path):
        print(f"파일을 찾을 수 없습니다: {image_path}")
        return

    # OCR 실행 및 디버그 모드 활성화
    results = ocr.run_ocr(image_path, debug=False)

    print("OCR 결과:")
    for idx, item in enumerate(results, start=1):
        accuracy_percentage = item[1][1] * 100  # Multiply by 100 to convert to percentage
        print(f"{idx}. Text: {item[1][0]}, Box: {item[0]}, Accuracy: {accuracy_percentage:.2f}%")

    # Save texts based on file type
    ocr.save_texts_based_on_file_type('Result.json')


if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("사용법: python rungopaddle.py <이미지 파일 경로>")
    else:
        main(sys.argv[1])
