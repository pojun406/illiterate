import sys
import os
import json
import cv2
from letsgopaddle import MyPaddleOCR
from image_util import crop_image_by_vector, get_vector_data_from_json

def main(image_path, json_data):
    ocr = MyPaddleOCR()

    if not os.path.exists(image_path):
        print(f"파일을 찾을 수 없습니다: {image_path}")
        return

    # Step 1: 제목 벡터만 받아와서 이미지 자르기
    vector_list = get_vector_data_from_json(json_data)

    if vector_list:
        # Step 2: 제목 부분 이미지 자르기 및 OCR 수행
        image = cv2.imread(image_path)
        cropped_title_image = crop_image_by_vector(image, vector_list[0])  # 첫 번째 벡터로 제목 부분 자름
        title_ocr_result = ocr.run_ocr_on_image(cropped_title_image)

        print(f"OCR 결과: {title_ocr_result}")

        # Step 3: paper_title_photo와 비교 (임의의 경로로 제공된 이미지를 비교)
        stored_images = {
            "a_type": "path/to/a_type_image.jpg",
            "b_type": "path/to/b_type_image.jpg",
            # 다른 문서 타입 이미지 경로 추가
        }
        document_type = ocr.determine_document_type(vector_list, image, stored_images)

        print(f"판별된 문서 타입: {document_type}")

        # Step 4: 해당 타입에 맞춰 전체 OCR 수행 및 JSON 저장
        if document_type != "Unknown":
            full_ocr_results = ocr.run_ocr(image_path)
            output_json = {
                "document_type": document_type,
                "ocr_results": full_ocr_results
            }
            output_file = os.path.join(os.path.dirname(image_path), f"{document_type}_ocr_results.json")
            with open(output_file, 'w', encoding='utf-8') as f:
                json.dump(output_json, f, ensure_ascii=False, indent=4)
            print(f"OCR 결과를 저장했습니다: {output_file}")

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("사용법: python rungopaddle.py <이미지 파일 경로> <JSON 데이터>")
    else:
        main(sys.argv[1].strip(), sys.argv[2].strip())
