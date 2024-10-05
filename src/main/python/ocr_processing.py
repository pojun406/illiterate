import sys
import os
import json
import cv2
import numpy as np
from PIL import Image
from MyFinalPPOCR import MyFinalPPOCR  # 커스텀 OCR 모듈
from db_connection import get_title_vector, get_images_from_db, get_vectors_by_type, get_document_info  # DB 관련 함수들
from image_preprocessing import crop_image_by_vector  # 이미지 자르기 관련 함수
from pathlib import Path
import locale

# 로케일 설정 (한국어 UTF-8)
locale.setlocale(locale.LC_ALL, 'ko_KR.UTF-8')

# OCR 객체 초기화
ocr = MyFinalPPOCR()

# 출력 인코딩을 UTF-8로 설정
sys.stdout.reconfigure(encoding='utf-8')

# 문자열로 된 벡터 좌표를 파싱하여 (x, y) 좌표 리스트로 변환하는 함수
def parse_vector(vector_str):
    """
    벡터 문자열을 파싱하여 (x, y) 좌표 튜플의 리스트로 변환.

    Args:
        vector_str (str): 문자열로 표현된 벡터 (좌표).

    Returns:
        list: (x, y) 좌표 튜플의 리스트.
    """
    # 벡터 문자열에서 숫자만 추출하고, 두 개씩 짝지어 좌표 튜플로 변환
    numbers = [float(num) for num in vector_str.replace('(', '').replace(')', '').split(',')]
    return [(numbers[i], numbers[i+1]) for i in range(0, len(numbers), 2)]

# 이미지 경로를 받아 처리하는 함수
def process_image(image_path):
    """
    이미지 경로를 받아 OCR 및 벡터 기반 이미지 처리를 수행.

    Args:
        image_path (str): 처리할 이미지의 파일 경로.

    Returns:
        dict: 처리된 결과를 JSON 형식으로 반환.
    """
    try:
        # 이미지 경로를 절대 경로로 변환 및 확인
        image_path = Path(image_path).resolve().as_posix()
        print(f"Attempting to read image from: {image_path}")
        print(f"File exists: {os.path.exists(image_path)}")  # 파일 존재 여부 출력
        print(f"File size: {os.path.getsize(image_path) if os.path.exists(image_path) else 'N/A'}")  # 파일 크기 출력

        results = []  # OCR 결과를 저장할 리스트 초기화

        # 이미지 파일을 열고 RGB 형식으로 변환
        with Image.open(image_path) as img:
            img = img.convert('RGB')  # 이미지를 RGB로 변환
            image = np.array(img)  # PIL 이미지를 numpy 배열로 변환

        print(f"Image shape: {image.shape}")  # 이미지 크기 출력
        print(f"Image dtype: {image.dtype}")  # 이미지 데이터 타입 출력

        # 이미지가 흑백(2차원 배열)이면 BGR로 변환, RGBA일 경우 알파 채널 제거
        if len(image.shape) == 2:
            image = cv2.cvtColor(image, cv2.COLOR_GRAY2BGR)  # 흑백 이미지를 BGR로 변환
        elif image.shape[2] == 4:
            image = cv2.cvtColor(image, cv2.COLOR_RGBA2BGR)  # RGBA 이미지에서 알파 채널 제거
        elif image.shape[2] != 3:
            raise ValueError(f"Unexpected image format: {image.shape}")  # 알 수 없는 이미지 형식 처리 시 오류 발생

        print(f"Processed image shape: {image.shape}")  # 변환된 이미지 크기 출력

        # 데이터베이스에서 타이틀 벡터 가져오기
        print("Getting title vectors from database...")
        title_vectors_dict = get_title_vector()  # 타이틀 벡터 가져오기
        print(f"Title vectors: {title_vectors_dict}")

        cropped_images = []  # 잘라낸 이미지 저장 리스트

        # 타이틀 벡터 처리
        print("Processing title vectors...")
        for document_index, vectors in title_vectors_dict.items():
            print(f"Processing document index: {document_index}")
            for vector in vectors:
                print(f"Processing vector: {vector}")
                if len(vector) == 4:
                    cropped_image = crop_image_by_vector(image, vector)  # 벡터를 이용해 이미지 자르기
                    cropped_images.append((document_index, cropped_image))  # 잘라낸 이미지를 리스트에 추가
                    print(f"Cropped image added for document index: {document_index}")
                else:
                    print(f"Invalid vector format for document_index {document_index}: {vector}")  # 잘못된 벡터 형식 처리

        # 데이터베이스에서 저장된 이미지 가져오기
        print("Getting stored images from database...")
        stored_images = get_images_from_db()  # 저장된 이미지 가져오기
        print(f"Stored images: {stored_images}")

        # 문서 유형 결정
        print("Determining document type...")
        document_index = ocr.determine_document_type(image_path, title_vectors_dict, stored_images)  # 문서 타입 결정
        print(f"Determined document type: {document_index}")

        # 결정된 문서 유형에 대한 벡터 가져오기
        print("Getting vectors for determined document type...")
        vectors = get_vectors_by_type(document_index)  # 해당 문서 유형의 벡터 가져오기
        print(f"Vectors: {vectors}")

        # 문서 정보 가져오기
        print("Getting document info...")
        document_index, img_info = get_document_info(document_index)  # 문서 정보 가져오기
        if img_info:
            # img_info에 있는 각 벡터 처리
            for vector_str, label in json.loads(img_info).items():
                print(f"\nProcessing vector for {label}: {vector_str}")

                # 벡터 문자열을 좌표 리스트로 변환
                vector = parse_vector(vector_str)

                # 벡터에 맞춰 이미지를 자른 후 저장
                cropped_image = crop_image_by_vector(cv2.imdecode(np.fromfile(image_path, dtype=np.uint8), cv2.IMREAD_COLOR), vector)
                cv2.imwrite(f"cropped_{label}.png", cropped_image)  # 잘라낸 이미지를 저장

                # OCR 실행 및 결과 처리
                try:
                    ocr_result = ocr.run_ocr(cropped_image)  # OCR 실행

                    print(f"Raw OCR result for {label}:")
                    print(json.dumps(ocr_result, ensure_ascii=False, indent=2))  # OCR 결과 출력

                    # OCR 결과 처리
                    if isinstance(ocr_result, dict) and 'error' in ocr_result:
                        processed_result = ocr_result  # 오류 처리
                    elif isinstance(ocr_result, list) and len(ocr_result) > 0:
                        combined_text = ' '.join([r['text'] for r in ocr_result])  # OCR 텍스트 결합
                        confidence = sum([r['confidence'] for r in ocr_result]) / len(ocr_result)  # 신뢰도 평균 계산
                        processed_result = {'text': combined_text, 'confidence': confidence}  # 처리된 결과 저장
                    else:
                        processed_result = {"error": "Unexpected OCR result format", "raw_result": str(ocr_result)}  # 예상치 못한 결과 처리

                    print(f"Processed OCR result for {label}: {processed_result}")
                except Exception as e:
                    # OCR 처리 중 오류 발생 시 처리
                    print(f"Error in OCR processing for {label}: {str(e)}")
                    processed_result = {"error": f"OCR processing failed: {str(e)}"}

                # OCR 결과를 리스트에 저장
                results.append({
                    "vector": vector_str,
                    "label": label,
                    "ocr_result": processed_result
                })

        # 최종 결과를 JSON 형식으로 반환
        final_result = {
            "document_index": document_index,
            "results": results
        }

        return final_result

    except Exception as e:
        # 이미지 처리 중 오류가 발생한 경우 처리
        print(f"Error processing image: {e}")
        print(f"Exception type: {type(e)}")
        print(f"Exception args: {e.args}")
        import traceback
        print("Traceback:")
        print(traceback.format_exc())  # 예외 발생 시 스택 트레이스 출력
        return {"error": str(e)}  # 오류 메시지 반환

# 메인 함수: 명령줄 인자로 이미지 경로가 제공된 경우 이미지 처리 실행
if __name__ == "__main__":
    if len(sys.argv) > 1:
        image_path = sys.argv[1]  # 첫 번째 인자로 이미지 경로 받기
        result = process_image(image_path)  # 이미지 처리 실행
        print("OCR 처리 결과가 ocr_result.json 파일에 저장되었습니다.")
    else:
        print("이미지 경로가 제공되지 않았습니다.")  # 이미지 경로가 제공되지 않았을 때 메시지 출력
