import os
import cv2
from paddleocr import PaddleOCR
from image_preprocessing import enhance_image, crop_image_by_vector
from skimage.metrics import structural_similarity as ssim
import numpy as np

class MyFinalPPOCR:
    def __init__(self):
        # PaddleOCR 모델 초기화
        # 한국어 모델을 사용하며, GPU 사용 및 로그 출력 비활성화
        self.ocr_model = PaddleOCR(
            lang='korean',
            use_angle_cls=True,
            use_gpu=True,
            show_log=False,
            enable_mkldnn=True
        )
        self.ocr_results = []

    def run_ocr(self, img):
        # 이미지 전처리 및 OCR 실행
        preprocessed_img = enhance_image(img)
        ocr_result = self.ocr_model.ocr(preprocessed_img, cls=False)

        print("Raw PaddleOCR result:")
        print(repr(ocr_result))

        # OCR 결과 처리 및 포맷팅
        if not ocr_result or ocr_result == [None]:
            #return {"error": "No OCR result found"}
            return {""} # error말고 빈값 요청해서 수정

        results = []
        for line in ocr_result:
            if isinstance(line, list) and len(line) > 0:
                for item in line:
                    if isinstance(item, list) and len(item) == 2:
                        box, (text, confidence) = item
                        results.append({
                            'text': text,
                            'confidence': float(confidence),
                            'box': [[float(coord) for coord in point] for point in box]
                        })
                    else:
                        print(f"Unexpected item format: {repr(item)}")
            else:
                print(f"Unexpected line format: {repr(line)}")

        if not results:
            return {"error": "No valid OCR results found"}

        return results

    def run_ocr_on_image(self, image_path):
        # 이미지 파일 읽기 및 OCR 실행
        print(f"Received image path: {image_path}")
        image_path = os.path.join(*image_path.split(os.path.sep))
        print(f"Normalized image path: {image_path}")
        print(f"Image exists: {os.path.exists(image_path)}")
        img = cv2.imread(image_path)
        if img is not None:
            if len(img.shape) == 2:
                img = cv2.cvtColor(img, cv2.COLOR_GRAY2BGR)
            elif img.shape[2] == 4:
                img = cv2.cvtColor(img, cv2.COLOR_RGBA2BGR)
            return self.run_ocr(img)
        else:
            print(f"Failed to read image: {image_path}")
            return {"error": f"Failed to read image: {image_path}"}

    def compare_images_ssim(self, img1, img2):
        # 두 이미지의 구조적 유사도(SSIM) 계산
        h1, w1 = img1.shape[:2]
        h2, w2 = img2.shape[:2]
        h = min(h1, h2)
        w = min(w1, w2)
        img1 = cv2.resize(img1, (w, h))
        img2 = cv2.resize(img2, (w, h))

        gray1 = cv2.cvtColor(img1, cv2.COLOR_BGR2GRAY)
        gray2 = cv2.cvtColor(img2, cv2.COLOR_BGR2GRAY)

        score, _ = ssim(gray1, gray2, full=True)
        return score

    def determine_document_type(self, image_path, vector_list, stored_images):
        # 문서 타입 결정
        image = cv2.imread(image_path)
        if image is None:
            return "Image not found"

        SIMILARITY_THRESHOLD = 0.7  # 이 값은 필요에 따라 조정할 수 있습니다

        best_match = None
        highest_similarity = -1

        for document_index, vectors in vector_list.items():
            for vector in vectors:
                cropped_image = crop_image_by_vector(image, vector)

                for stored_image_path in stored_images:
                    stored_image = cv2.imread(stored_image_path)
                    if stored_image is None:
                        continue

                    similarity = self.compare_images_ssim(cropped_image, stored_image)
                    if similarity > highest_similarity:
                        highest_similarity = similarity
                        best_match = document_index

        if highest_similarity < SIMILARITY_THRESHOLD:
            return "Unknown"

        return best_match if best_match else "Unknown"