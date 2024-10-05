import os
import cv2
from paddleocr import PaddleOCR
from image_preprocessing import enhance_image, crop_image_by_vector  # 이미지 전처리 및 자르기 관련 함수
from skimage.metrics import structural_similarity as ssim  # SSIM 계산을 위한 모듈
import numpy as np

class MyFinalPPOCR:
    def __init__(self):
        # PaddleOCR 모델 초기화
        # 한국어 모델을 사용하며, GPU 사용 및 로그 출력 비활성화
        self.ocr_model = PaddleOCR(
            lang='korean',  # 한국어 OCR 모델
            use_angle_cls=True,  # 텍스트 각도 분류 활성화
            use_gpu=True,  # GPU 사용
            show_log=False,  # 로그 출력 비활성화
            enable_mkldnn=True  # MKL-DNN 사용으로 성능 최적화
        )
        self.ocr_results = []  # OCR 결과를 저장하기 위한 리스트

    def run_ocr(self, img):
        # 이미지 전처리 후 OCR 실행
        preprocessed_img = enhance_image(img)  # 이미지 전처리 (선명도 향상 등)
        ocr_result = self.ocr_model.ocr(preprocessed_img, cls=False)  # 전처리된 이미지에 대해 OCR 실행

        print("Raw PaddleOCR result:")  # OCR 원본 결과 출력
        print(repr(ocr_result))  # 결과 디버깅용 출력

        # OCR 결과가 없을 경우 처리
        if not ocr_result or ocr_result == [None]:
            return {"error": "No OCR result found"}  # 결과가 없으면 오류 반환

        results = []  # OCR 결과 저장 리스트 초기화
        for line in ocr_result:  # OCR 결과의 각 줄을 처리
            if isinstance(line, list) and len(line) > 0:
                for item in line:
                    if isinstance(item, list) and len(item) == 2:
                        # 텍스트와 신뢰도 추출 후 결과 저장
                        box, (text, confidence) = item
                        results.append({
                            'text': text,  # 추출된 텍스트
                            'confidence': float(confidence),  # 신뢰도
                            'box': [[float(coord) for coord in point] for point in box]  # 텍스트 좌표
                        })
                    else:
                        print(f"Unexpected item format: {repr(item)}")  # 예상치 못한 포맷 처리
            else:
                print(f"Unexpected line format: {repr(line)}")  # 예상치 못한 줄 포맷 처리

        # 유효한 결과가 없을 경우
        if not results:
            return {"error": "No valid OCR results found"}  # 오류 반환

        return results  # OCR 결과 반환

    def run_ocr_on_image(self, image_path):
        # 이미지 경로를 받아 OCR 실행
        print(f"Received image path: {image_path}")  # 입력받은 경로 출력
        image_path = os.path.join(*image_path.split(os.path.sep))  # 경로 표준화
        print(f"Normalized image path: {image_path}")  # 표준화된 경로 출력
        print(f"Image exists: {os.path.exists(image_path)}")  # 이미지 존재 여부 확인

        # 이미지 읽기 및 OCR 처리
        img = cv2.imread(image_path)
        if img is not None:
            if len(img.shape) == 2:  # 흑백 이미지 처리
                img = cv2.cvtColor(img, cv2.COLOR_GRAY2BGR)
            elif img.shape[2] == 4:  # RGBA 이미지 처리 (알파 채널 제거)
                img = cv2.cvtColor(img, cv2.COLOR_RGBA2BGR)
            return self.run_ocr(img)  # OCR 실행 후 결과 반환
        else:
            print(f"Failed to read image: {image_path}")  # 이미지 읽기 실패 시
            return {"error": f"Failed to read image: {image_path}"}  # 오류 반환

    def compare_images_ssim(self, img1, img2):
        # 두 이미지의 구조적 유사도(SSIM) 계산
        h1, w1 = img1.shape[:2]  # 첫 번째 이미지의 높이, 너비
        h2, w2 = img2.shape[:2]  # 두 번째 이미지의 높이, 너비
        h = min(h1, h2)  # 두 이미지 중 작은 높이
        w = min(w1, w2)  # 두 이미지 중 작은 너비
        img1 = cv2.resize(img1, (w, h))  # 첫 번째 이미지 크기 조정
        img2 = cv2.resize(img2, (w, h))  # 두 번째 이미지 크기 조정

        gray1 = cv2.cvtColor(img1, cv2.COLOR_BGR2GRAY)  # 첫 번째 이미지를 그레이스케일로 변환
        gray2 = cv2.cvtColor(img2, cv2.COLOR_BGR2GRAY)  # 두 번째 이미지를 그레이스케일로 변환

        score, _ = ssim(gray1, gray2, full=True)  # SSIM 계산
        return score  # SSIM 점수 반환

    def determine_document_type(self, image_path, vector_list, stored_images):
        # 문서 타입 결정
        image = cv2.imread(image_path)  # 입력된 이미지 읽기
        if image is None:
            return "Image not found"  # 이미지 파일이 없을 경우 오류 메시지 반환

        best_match = None  # 가장 유사한 문서 타입 저장 변수
        highest_similarity = -1  # 최대 유사도 초기화

        # 벡터 리스트와 저장된 이미지들을 비교하여 가장 유사한 문서 타입 결정
        for document_index, vectors in vector_list.items():
            for vector in vectors:
                cropped_image = crop_image_by_vector(image, vector)  # 주어진 벡터로 이미지 자르기

                for stored_image_path in stored_images:  # 저장된 이미지와 비교
                    stored_image = cv2.imread(stored_image_path)
                    if stored_image is None:
                        continue  # 이미지 읽기 실패 시 건너뜀

                    similarity = self.compare_images_ssim(cropped_image, stored_image)  # SSIM으로 유사도 계산
                    if similarity > highest_similarity:  # 더 높은 유사도 발견 시 갱신
                        highest_similarity = similarity
                        best_match = document_index  # 가장 유사한 문서 타입 갱신

        return best_match if best_match else "Unknown"  # 가장 유사한 문서 타입 반환, 없을 시 "Unknown" 반환
