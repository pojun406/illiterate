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

    def save_texts_by_indexes(self, indexes, output_file):
        """지정된 인덱스의 텍스트를 Result.json 파일로 저장하는 함수"""
        selected_texts = [{"key": self.ocr_results[i - 1][1][0]} for i in indexes if i - 1 < len(self.ocr_results)]
        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump(selected_texts, f, ensure_ascii=False, indent=4)
