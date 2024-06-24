import cv2
import json
import os
import re
from paddleocr import PaddleOCR
from image_util import plt_imshow, enhance_image, resize_image
import numpy as np
import matplotlib.pyplot as plt

class MyPaddleOCR:
    """PaddleOCR를 이용한 OCR 클래스"""

    def __init__(self, **kwargs):
        """클래스 초기화 함수"""
        self.ocr_models = {
            'korean': PaddleOCR(lang='korean'),
            'chinese': PaddleOCR(lang='ch'),
            'english': PaddleOCR(lang='en')
        }
        self.img_path = None
        self.ocr_results = []

    def run_ocr(self, img_path: str, debug: bool = False):
        """OCR을 실행하고 결과를 반환하는 함수"""
        self.img_path = img_path
        img = cv2.imread(img_path)

        # 이미지 확대
        scale = 2.0
        resized_img = resize_image(img, scale=scale)

        # 이미지 전처리
        preprocessed_img = enhance_image(resized_img)

        for lang, ocr in self.ocr_models.items():
            result = ocr.ocr(preprocessed_img, cls=False)
            self.ocr_results.extend(result[0])

        if debug:
            self.show_img_with_ocr(preprocessed_img)

        # OCR 결과를 JSON 파일로 저장
        self.save_ocr_result_to_individual_files('savetext')

        return self.ocr_results

    def save_ocr_result_to_individual_files(self, output_directory: str):
        """OCR 결과를 개별 JSON 파일로 저장하는 함수"""
        if not os.path.exists(output_directory):
            os.makedirs(output_directory)

        for line in self.ocr_results:
            text = re.sub(r'[\\/*?:"<>|]', "", line[1][0])  # Remove any invalid characters for filenames
            output_file = os.path.join(output_directory, f"{text}.json")
            with open(output_file, 'w', encoding='utf-8') as f:
                json.dump(line, f, ensure_ascii=False, indent=4)

    def show_img_with_ocr(self, img):
        """OCR 결과를 이미지에 표시하는 함수"""
        for line in self.ocr_results:
            text = line[1][0]
            box = line[0]
            # Convert box coordinates to integers and tuple format
            box = [(int(x), int(y)) for x, y in box]
            img = cv2.polylines(img, [np.array(box).astype(np.int32)], isClosed=True, color=(0, 255, 0), thickness=2)
            img = cv2.putText(img, text, tuple(box[0]), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2, cv2.LINE_AA)

        # Display the image using matplotlib
        plt.figure(figsize=(10, 10))
        plt.imshow(cv2.cvtColor(img, cv2.COLOR_BGR2RGB))
        plt.title('OCR Result')
        plt.axis('off')
        plt.show()

    def save_texts_based_on_file_type(self, output_file):
        """파일 유형에 따라 텍스트를 저장하는 함수"""
        # Define the index orders for the two file types
        file_type_1_indexes = [21, 266, 264, 268, 38, 48, 49, 50, 53, 54, 56, 57, 275, 52, 55, 63, 64, 65, 71, 66, 73,
                               74, 78, 79, 287, 81, 288, 98, 100, 101, 103, 112, 111, 113, 114, 117, 119, 120, 121, 147,
                               148, 205, 207, 208, 213, 214, 215, 216]
        file_type_2_indexes = [31, 36, 37, 32, 33, 34, 45, 46, 66, 485, 64, 65, 73, 74, 78, 75, 76, 97, 95, 99, 435,
                               437, 443, 447, 444, 439, 440, 441, 457]

        # Keywords to identify the file type
        keyword_file_type_1 = "\'생\'신\'고"
        keyword_file_type_2 = '전입신고서세대'

        detected_file_type = None

        # Determine the file type based on the presence of keywords in OCR results
        for item in self.ocr_results:
            text = item[1][0]
            # Debug: Print each text to verify the content
            print("OCR Text:", text)
            if keyword_file_type_1 in text:
                detected_file_type = 1
                break
            elif keyword_file_type_2 in text:
                detected_file_type = 2
                break

        print("Detected File Type:", detected_file_type)

        if detected_file_type == 1:
            selected_indexes = file_type_1_indexes
            title = "출생신고서"
        elif detected_file_type == 2:
            selected_indexes = file_type_2_indexes
            title = "전입신고서"
        else:
            selected_indexes = []
            title = "Unknown"

        print("Selected Indexes:", selected_indexes)

        selected_texts = [{"key": self.ocr_results[i - 1][1][0]} for i in selected_indexes if
                          i - 1 < len(self.ocr_results)]

        print("Selected Texts:", selected_texts)

        result_data = {
            "title": title,
            "texts": selected_texts
        }

        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump(result_data, f, ensure_ascii=False, indent=4)

        print(f"OCR 결과를 파일에 저장했습니다: {output_file}")