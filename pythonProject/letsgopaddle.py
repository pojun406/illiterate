import cv2
import os
import json
from paddleocr import PaddleOCR
from image_util import enhance_image, crop_image_by_vector
import numpy as np

class MyPaddleOCR:
    """PaddleOCR를 이용한 OCR 클래스"""

    def __init__(self, **kwargs):
        self.ocr_models = {
            'korean': PaddleOCR(lang='korean'),
            'chinese': PaddleOCR(lang='ch'),
            'english': PaddleOCR(lang='en')
        }
        self.ocr_results = []

    def run_ocr(self, img_path):
        """OCR을 실행하고 결과를 반환하는 함수"""
        img = cv2.imread(img_path)
        preprocessed_img = enhance_image(img)

        for lang, ocr in self.ocr_models.items():
            result = ocr.ocr(preprocessed_img, cls=False)
            self.ocr_results.extend(result[0])

        return self.ocr_results

    def crop_title_from_image(self, image, title_vector):
        """이미지에서 제목 벡터 부분을 자르고 OCR 실행"""
        cropped_image = crop_image_by_vector(image, title_vector)
        return self.run_ocr_on_image(cropped_image)

    def run_ocr_on_image(self, image):
        """이미지에서 바로 OCR을 실행"""
        preprocessed_img = enhance_image(image)
        results = []
        for lang, ocr in self.ocr_models.items():
            result = ocr.ocr(preprocessed_img, cls=False)
            results.extend(result[0])
        return results

    def compare_images(self, image1, image2):
        """두 이미지를 비교하여 유사도를 반환"""
        img1 = cv2.cvtColor(image1, cv2.COLOR_BGR2GRAY)
        img2 = cv2.cvtColor(image2, cv2.COLOR_BGR2GRAY)
        difference = cv2.absdiff(img1, img2)
        return np.sum(difference)

    def determine_document_type(self, vector_list, image, stored_images):
        """제공된 이미지와 저장된 이미지를 비교하여 문서 타입을 판별"""
        for vector in vector_list:
            cropped_image = crop_image_by_vector(image, vector)

            # 저장된 이미지들과 비교하여 가장 유사한 이미지를 찾음
            for document_type, stored_image_path in stored_images.items():
                stored_image = cv2.imread(stored_image_path)
                if stored_image is None:
                    continue
                similarity = self.compare_images(cropped_image, stored_image)
                if similarity < threshold:  # 임계값을 설정해 유사도 비교
                    return document_type

        return "Unknown"  # 유사한 문서가 없으면 Unknown 반환
