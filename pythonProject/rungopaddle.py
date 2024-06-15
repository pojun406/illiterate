from letsgopaddle import MyPaddleOCR
import sys
import os
import io

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')


def main(image_path):
    ocr = MyPaddleOCR()

    print(f"Python executable: {sys.executable}")
    print(f"Python version: {sys.version}")
    print(f"Conda environment: {os.environ.get('CONDA_DEFAULT_ENV')}")
    print(f"Environment PATH: {os.environ.get('PATH')}")

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
    output_dir = os.path.dirname(image_path)
    output_file = os.path.join(output_dir, 'result.json')
    ocr.save_texts_based_on_file_type(output_file)
    print(f"OCR 결과를 파일에 저장했습니다: {output_file}")

    # 파일 존재 여부 확인
    if os.path.exists(output_file):
        print(f"파일이 성공적으로 생성되었습니다: {output_file}", flush=True)
    else:
        print(f"파일 생성 실패: {output_file}")

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("사용법: python rungopaddle.py <이미지 파일 경로>")
    else:
        main(sys.argv[1].strip())
