from paddleocr import PaddleOCR
import os
from PIL import Image, ImageDraw

os.environ['KMP_DUPLICATE_LIB_OK'] = 'True'

def draw_ocr_result(image_path, ocr_result):
    # 이미지 열기
    image = Image.open(image_path)
    draw = ImageDraw.Draw(image)

    # OCR 결과를 이미지에 그리기
    for box, text in ocr_result:
        # 각 텍스트 박스의 좌표 추출
        x1, y1 = box[0]
        x2, y2 = box[2]

        # 텍스트 박스 그리기
        draw.rectangle([x1, y1, x2, y2], outline="red")

        # 텍스트 쓰기
        draw.text((x1, y1 - 20), str(text), fill="red")  # 텍스트를 문자열로 변환하여 사용

    # 이미지 표시
    image.show()


ocr = PaddleOCR(lang="korean")

# 이미지 경로
img_path = "/photo/paaa.jpg"

# OCR 실행 및 결과 저장
result = ocr.ocr(img_path, cls=False)
ocr_result = [(line[0][0], line[1][0]) for line in result[0]]

# 이미지와 OCR 결과 함께 표시
draw_ocr_result(img_path, ocr_result)
