import sys
import os
import json
import cv2
import numpy as np
import sys

from PIL import Image

from MyFinalPPOCR import MyFinalPPOCR  # SSIM 방식이 적용된 MyFinalPPOCR 클래스
from db_connection import get_title_vector, get_images_from_db, get_vectors_by_type, get_document_info
from image_preprocessing import crop_image_by_vector

from pathlib import Path

import locale
locale.setlocale(locale.LC_ALL, 'ko_KR.UTF-8')

ocr = MyFinalPPOCR()
sys.stdout.reconfigure(encoding='utf-8')

def process_image(image_path):
    """
    이미지 경로를 받아 OCR 처리하고 문서 타입을 결정하는 함수.
    """
    try:
        image_path = Path(image_path).resolve().as_posix()
        print(f"Attempting to read image from: {image_path}")
        print(f"File exists: {os.path.exists(image_path)}")
        print(f"File size: {os.path.getsize(image_path) if os.path.exists(image_path) else 'N/A'}")

        with Image.open(image_path) as img:
            img = img.convert('RGB')
            image = np.array(img)

        print(f"Image shape: {image.shape}")
        print(f"Image dtype: {image.dtype}")

        if len(image.shape) == 2:  # 그레이스케일 이미지인 경우
            image = cv2.cvtColor(image, cv2.COLOR_GRAY2BGR)
        elif image.shape[2] == 4:  # RGBA 이미지인 경우
            image = cv2.cvtColor(image, cv2.COLOR_RGBA2BGR)
        elif image.shape[2] != 3:  # BGR이 아닌 경우
            raise ValueError(f"Unexpected image format: {image.shape}")

        print(f"Processed image shape: {image.shape}")

        # 2. DB에서 제목 벡터 가져오기
        title_vectors = get_title_vector()

        # 3. 제목 부분 잘라내기
        cropped_images = []
        for vector in title_vectors:
            if len(vector) == 4:
                (x1, y1), (x2, y2), (x3, y3), (x4, y4) = vector
                cropped_image = crop_image_by_vector(image, [(x1, y1), (x2, y2), (x3, y3), (x4, y4)])
                cropped_images.append(cropped_image)
            else:
                print(f"Invalid vector format: {vector}")

        # 4. DB에서 title_img 가져오기
        db_images = get_images_from_db()

        # 5. 이미지 비교 및 가장 유사한 이미지 선택
        best_match = None
        highest_similarity = -1
        for i, cropped_image in enumerate(cropped_images):
            for j, db_image in enumerate(db_images):
                similarity = ocr.compare_images_ssim(cropped_image, cv2.imread(db_image))
                if similarity > highest_similarity:
                    highest_similarity = similarity
                    best_match = (i, j)

        if best_match is None:
            return {"error": "No matching image found"}

        # 선택된 이미지의 document_index와 img_info 가져오기
        result = get_document_info(best_match[1])
        if result is None:
            return {"error": "Document info not found"}
        document_index, img_info = result

        # 6. img_info의 vector로 이미지 자르기 및 OCR 실행
        results = []
        for vector in json.loads(img_info):
            cropped_image = crop_image_by_vector(cv2.imdecode(np.fromfile(image_path, dtype=np.uint8), cv2.IMREAD_COLOR), vector)
            ocr_result = ocr.run_ocr(cropped_image)
            results.append({
                "vector": vector,
                "ocr_result": ocr_result
            })

        # 7. 결과를 JSON 형식으로 변환하고 파일로 저장
        final_result = {
            "document_index": document_index,
            "results": results
        }

        with open('ocr_result.json', 'w', encoding='utf-8') as f:
            json.dump(final_result, f, ensure_ascii=False, indent=2)
        print("OCR 처리 결과가 ocr_result.json 파일에 저장되었습니다.")

        return final_result

    except Exception as e:
        print(f"Error processing image: {e}")
        print(f"Exception type: {type(e)}")
        print(f"Exception args: {e.args}")
        return {"error": str(e)}


if __name__ == "__main__":
    # Command Line에서 이미지 경로 받기
    if len(sys.argv) > 1:
        image_path = sys.argv[1]
        print(f"이미지 경로: {image_path}")

        # 이미지 경로로 OCR 처리 실행
        result = process_image(image_path)

        # OCR 결과를 JSON 파일로 저장
        print("OCR 처리 결과가 ocr_result.json 파일에 저장되었습니다.")
    else:
        print("이미지 경로가 제공되지 않았습니다.")
