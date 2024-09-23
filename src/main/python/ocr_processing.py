import sys
import os
import json
import cv2
from MyFinalPPOCR import MyFinalPPOCR  # SSIM 방식이 적용된 MyFinalPPOCR 클래스
from db_connection import get_title_vector, get_images_from_db, get_vectors_by_type, get_document_info
from image_preprocessing import crop_image_by_vector


def process_image(image_path):
    """
    이미지 경로를 받아 OCR 처리하고 문서 타입을 결정하는 함수.
    """
    ocr = MyFinalPPOCR()
    
    # 2. DB에서 제목 벡터 가져오기
    title_vectors = get_title_vector()
    
    # 3. 제목 부분 잘라내기
    cropped_images = []
    for vector in title_vectors:
        image = cv2.imread(image_path)
        cropped_image = crop_image_by_vector(image, vector)
        cropped_images.append(cropped_image)
    
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
        cropped_image = crop_image_by_vector(cv2.imread(image_path), vector)
        ocr_result = ocr.run_ocr(cropped_image)
        results.append({"vector": vector, "ocr_result": ocr_result})
    
    # 7. 결과 반환
    return {"document_index": document_index, "results": results}


if __name__ == "__main__":
    # Command Line에서 이미지 경로 받기
    if len(sys.argv) > 1:
        image_path = sys.argv[1]
        print(f"이미지 경로: {image_path}")

        # 이미지 경로로 OCR 처리 실행
        ocr_results = process_image(image_path)

        # OCR 결과 출력
        print("OCR 처리 결과:")
        print(ocr_results)
    else:
        print("이미지 경로가 제공되지 않았습니다.")
