import os
import sys
from letsgopaddle import MyPaddleOCR

def main(temp_file_path):
    ocr = MyPaddleOCR()

    if not os.path.exists(temp_file_path):
        print(f"파일을 찾을 수 없습니다: {temp_file_path}")
        return

    # OCR 실행 및 디버그 모드 활성화
    result = ocr.run_ocr(temp_file_path, debug=True)
    print("OCR 결과:")
    for item in result:
        print(item)

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("Usage: python rungopaddle.py <TempFile>")
        sys.exit(1)

    temp_file_path = sys.argv[1]
    main(temp_file_path)