import cv2
import json
import os
import re
from paddleocr import PaddleOCR
from utils.image_util import plt_imshow, put_text

class MyPaddleOCR:
    def __init__(self, lang: str = "korean", **kwargs):
        self.lang = lang
        self._ocr = PaddleOCR(lang="korean")
        self.img_path = None
        self.ocr_result = {}

    def get_ocr_result(self):
        return self.ocr_result

    def clean_text_for_filename(self, text: str) -> str:
        # 파일 이름에 사용할 수 없는 문자 제거 (윈도우즈 파일 시스템 기준)
        cleaned_text = re.sub(r'[\\/:*?"<>|]', '', text)
        return cleaned_text

    def run_ocr(self, img_path: str, debug: bool = False):
        self.img_path = img_path
        result = self._ocr.ocr(img_path, cls=False)
        self.ocr_result = result[0]

        if debug:
            self.show_img_with_ocr()

        # OCR 결과를 JSON 파일로 저장
        self.save_ocr_result_to_individual_files('savetext')

    def save_ocr_result_to_individual_files(self, output_directory: str):
        if not os.path.exists(output_directory):
            os.makedirs(output_directory)

        if self.ocr_result:
            for i, text_result in enumerate(self.ocr_result):
                text = text_result[1][0]
                # JSON 파일 제목으로 텍스트 내용 사용
                cleaned_text = self.clean_text_for_filename(text)
                output_file_path = os.path.join(output_directory, f'{cleaned_text}.json')

                with open(output_file_path, 'w', encoding='utf-8') as f:
                    json.dump({"text": text}, f, ensure_ascii=False, indent=4)

                print(f"텍스트가 {output_file_path} 파일에 저장되었습니다.")

    def show_img_with_ocr(self):
        img = cv2.imread(self.img_path)
        roi_img = img.copy()

        for text_result in self.ocr_result:
            text = text_result[1][0]
            tlX = int(text_result[0][0][0])
            tlY = int(text_result[0][0][1])
            trX = int(text_result[0][1][0])
            trY = int(text_result[0][1][1])
            brX = int(text_result[0][2][0])
            brY = int(text_result[0][2][1])
            blX = int(text_result[0][3][0])
            blY = int(text_result[0][3][1])

            pts = ((tlX, tlY), (trX, trY), (brX, brY), (blX, blY))

            topLeft = pts[0]
            topRight = pts[1]
            bottomRight = pts[2]
            bottomLeft = pts[3]

            cv2.line(roi_img, topLeft, topRight, (0, 255, 0), 2)
            cv2.line(roi_img, topRight, bottomRight, (0, 255, 0), 2)
            cv2.line(roi_img, bottomRight, bottomLeft, (0, 255, 0), 2)
            cv2.line(roi_img, bottomLeft, topLeft, (0, 255, 0), 2)
            roi_img = put_text(roi_img, text, topLeft[0], topLeft[1] - 20, font_size=15)

            print(text)

        plt_imshow(["Original", "ROI"], [img, roi_img], figsize=(16, 10))

class OCRWithAnalysis:
    def __init__(self, ocr_results_directory: str):
        self.ocr_results_directory = ocr_results_directory

    def analyze_ocr_result(self, target_title: str):
        for filename in os.listdir(self.ocr_results_directory):
            if filename.endswith('.json'):
                with open(os.path.join(self.ocr_results_directory, filename), 'r', encoding='utf-8') as f:
                    result = json.load(f)
                    if result['text'] == target_title:
                        print(f"'{target_title}'로 이루어진 파일을 찾았습니다. : {filename}")
                        return True

        print(f"'{target_title}'로 이루어진 파일은 없습니다.")
        return False

# 실행 코드
ocr = MyPaddleOCR()


# 사용자로부터 이미지 파일 경로 입력 받기
base_directory = 'C:/Users/404ST011/PycharmProjects/pythonProject/photo'
image_filename = input("이미지 파일 이름을 입력하세요: ")
image_path = os.path.join(base_directory, image_filename)
result = ocr.run_ocr(image_path, debug=True)

# OCR 결과를 저장한 JSON 파일들을 분석하여 특정 제목이 있는지 확인
ocr_analyzer = OCRWithAnalysis('C:\\Users\\404ST011\PycharmProjects\pythonProject\savetext')

# 사용자로부터 특정 제목 입력 받기
target_title = input("특정 제목을 입력하세요: ")

ocr_analyzer.analyze_ocr_result(target_title)