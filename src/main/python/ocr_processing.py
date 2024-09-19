import sys
import os
import json
import cv2
from MyFinalPPOCR import MyFinalPPOCR  # SSIM 방식이 적용된 MyFinalPPOCR 클래스
from db_connection import get_title_vector, get_images_from_db, get_vectors_by_type
from image_preprocessing import crop_image_by_vector


def save_cropped_title_image(image_path, title_vector):
    """
    DB에서 가져온 벡터값으로 이미지를 자르고 save_title 디렉토리에 저장.
    """
    image = cv2.imread(image_path)
    if image is None:
        raise FileNotFoundError(f"이미지를 찾을 수 없습니다: {image_path}")

    cropped_image = crop_image_by_vector(image, title_vector)
    save_path = os.path.join("save_title", "title_cropped.png")
    cv2.imwrite(save_path, cropped_image)
    return save_path


def compare_and_select_image(save_title_dir, db_image_paths):
    """
    save_title 디렉토리의 이미지와 DB에 저장된 이미지들 중
    SSIM을 사용하여 가장 유사한 것을 찾아냅니다.

    Args:
        save_title_dir (str): save_title 디렉토리 경로.
        db_image_paths (list): DB에 저장된 이미지 경로 리스트.

    Returns:
        str: 가장 유사한 이미지 파일 이름.
    """
    ocr = MyFinalPPOCR()
    title_image_path = os.path.join(save_title_dir, "title_cropped.png")
    title_image = cv2.imread(title_image_path)

    best_match = None
    highest_similarity = -1

    for db_image_path in db_image_paths:
        db_image = cv2.imread(db_image_path)
        if db_image is None:
            continue
        # SSIM을 사용해 비교
        similarity = ocr.compare_images_ssim(title_image, db_image)
        if similarity > highest_similarity:
            highest_similarity = similarity
            best_match = os.path.basename(db_image_path).split('.')[0]  # 확장자 제거하고 파일명만 반환

    return best_match


def process_image_by_type(image_path, image_type):
    """
    결정된 이미지 타입에 맞춰 벡터값을 가져와 이미지 크롭 후 OCR을 실행합니다.

    Args:
        image_path (str): 처리할 이미지 경로
        image_type (str): 결정된 이미지 타입
    """
    ocr = MyFinalPPOCR()
    vectors = get_vectors_by_type(image_type)  # 해당 타입의 벡터값 가져오기

    results = []
    for vector in vectors:
        cropped_image = crop_image_by_vector(cv2.imread(image_path), vector)
        ocr_result = ocr.run_ocr(cropped_image)
        results.append({"vector": vector, "ocr_result": ocr_result})

    return results


def save_and_send_results(ocr_results, output_file="ocr_result.json"):
    """
    OCR 결과와 벡터값을 JSON 파일로 저장하고 이를 백엔드로 전송.

    Args:
        ocr_results (list): OCR 결과 리스트
        output_file (str): 저장할 JSON 파일 이름
    """
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(ocr_results, f, ensure_ascii=False, indent=4)

    print(f"결과가 {output_file}에 저장되었습니다.")
    send_json_to_backend(output_file)


def send_json_to_backend(json_file):
    """
    JSON 파일을 백엔드로 전송합니다.

    Args:
        json_file (str): 전송할 JSON 파일 경로
    """
    import requests
    url = "http://your-backend-url.com/api/ocr_result"  # 백엔드 서버 URL
    with open(json_file, 'rb') as f:
        files = {'file': f}
        response = requests.post(url, files=files)

    if response.status_code == 200:
        print("백엔드로 성공적으로 전송되었습니다.")
    else:
        print(f"전송 중 오류 발생: {response.status_code}")


def process_image(image_path):
    """
    이미지 경로를 받아 OCR 처리하고 문서 타입을 결정하는 함수.
    """
    # MyFinalPPOCR 인스턴스 생성
    ocr = MyFinalPPOCR()

    # DB에서 제목 벡터 가져오기
    title_vector = get_title_vector()

    # 제목 부분을 자르고 저장
    cropped_image_path = save_cropped_title_image(image_path, title_vector)

    # DB에 저장된 이미지 경로들 가져오기
    db_image_paths = get_images_from_db()

    # 가장 유사한 제목 이미지 결정
    selected_type = compare_and_select_image("save_title", db_image_paths)

    # 결정된 이미지 타입에 맞춰 OCR 실행
    ocr_results = process_image_by_type(image_path, selected_type)

    # OCR 결과 반환
    return ocr_results


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
