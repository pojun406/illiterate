import cv2
import json
import os
import re
from paddleocr import PaddleOCR
from image_util import plt_imshow, enhance_image, resize_image

class MyPaddleOCR:
    """PaddleOCR를 이용한 OCR 클래스"""

    def __init__(self, lang: str = "korean", **kwargs):
        """클래스 초기화 함수"""
        self.lang = lang
        self._ocr = PaddleOCR(lang="korean")
        self.img_path = None
        self.ocr_result = {}

    def run_ocr(self, img_path: str, debug: bool = False):
        """OCR을 실행하고 결과를 반환하는 함수"""
        self.img_path = img_path
        img = cv2.imread(img_path)

        # 이미지 확대
        scale = 2.0
        resized_img = resize_image(img, scale=scale)

        # 이미지 전처리
        preprocessed_img = enhance_image(resized_img)

        result = self._ocr.ocr(preprocessed_img, cls=False)
        self.ocr_result = result[0]

        if debug:
            self.show_img_with_ocr(preprocessed_img)

        # OCR 결과를 JSON 파일로 저장
        self.save_ocr_result_to_individual_files('savetext')

        # 특정 위치의 텍스트를 묶어서 JSON 파일로 저장
        self.save_grouped_text_as_json('savetext')

        return self.ocr_result

    def save_ocr_result_to_individual_files(self, output_directory: str):
        """OCR 결과를 개별 JSON 파일로 저장하는 함수"""
        if not os.path.exists(output_directory):
            os.makedirs(output_directory)

        if self.ocr_result:
            for i, text_result in enumerate(self.ocr_result):
                text = text_result[1][0]
                cleaned_text = self.clean_text_for_filename(text)
                output_file_path = os.path.join(output_directory, f'{cleaned_text}.json')

                with open(output_file_path, 'w', encoding='utf-8') as f:
                    json.dump({"text": text}, f, ensure_ascii=False, indent=4)

                print(f"텍스트가 {output_file_path} 파일에 저장되었습니다.")

    def save_grouped_text_as_json(self, output_directory: str):
        """특정 위치의 텍스트를 그룹화하여 JSON 파일로 저장하는 함수"""
        grouped_text_dict = {}

        for text_result in self.ocr_result:
            text = text_result[1][0]
            bounding_box = text_result[0]

            if '성명' in text:
                name = text.split(':')[-1].strip()
                grouped_text_dict['성명'] = name
            elif '주민등록번호' in text:
                id_number = text.split(':')[-1].strip()
                grouped_text_dict['주민등록번호'] = id_number

        if grouped_text_dict:
            output_file_path = os.path.join(output_directory, 'grouped_text.json')
            with open(output_file_path, 'w', encoding='utf-8') as f:
                json.dump(grouped_text_dict, f, ensure_ascii=False, indent=4)

            print(f"묶인 텍스트 정보가 {output_file_path} 파일에 저장되었습니다.")

    def clean_text_for_filename(self, text: str) -> str:
        """파일 이름으로 사용할 수 있는 텍스트로 변환하는 함수"""
        cleaned_text = re.sub(r'[\\/:*?"<>|]', '', text)
        return cleaned_text

    def show_img_with_ocr(self, img, dpi=200):
        """OCR 결과를 이미지에 표시하는 함수"""
        for box, (text, score) in self.ocr_result:
            x1, y1 = map(int, box[0])
            x2, y2 = map(int, box[2])
            cv2.rectangle(img, (x1, y1), (x2, y2), (0, 255, 0), 2)
            cv2.putText(img, f'{text} ({score:.2f})', (x1, y1 - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)

        plt_imshow('OCR Results', img, figsize=(16, 12), dpi=dpi)

# 실행 코드
if __name__ == '__main__':
    ocr = MyPaddleOCR()

    base_directory = 'C:/Users/404ST011/PycharmProjects/pythonProject/photo'
    image_filename = input("이미지 파일 이름을 입력하세요: ")
    image_path = os.path.join(base_directory, image_filename)
    result = ocr.run_ocr(image_path, debug=True)
    print(result)