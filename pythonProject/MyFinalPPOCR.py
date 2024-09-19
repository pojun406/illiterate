import cv2
from paddleocr import PaddleOCR
from image_preprocessing import enhance_image, crop_image_by_vector
from skimage.metrics import structural_similarity as ssim


class MyFinalPPOCR:
    def __init__(self):
        # 다양한 언어에 대한 OCR 모델 인스턴스 생성
        self.ocr_models = {
            'korean': PaddleOCR(lang='korean'),
            'chinese': PaddleOCR(lang='ch'),
            'english': PaddleOCR(lang='en')
        }
        # OCR 결과를 저장할 리스트
        self.ocr_results = []

    def run_ocr(self, img):
        """
        주어진 이미지에 대해 모든 언어로 OCR 실행 후,
        가장 높은 정확도를 가진 결과를 반환.

        Args:
            img (numpy.ndarray): OCR을 실행할 이미지.

        Returns:
            dict: 가장 높은 정확도의 OCR 결과.
        """
        preprocessed_img = enhance_image(img)
        best_result = None
        highest_confidence = 0

        for lang, ocr_model in self.ocr_models.items():
            ocr_result = ocr_model.ocr(preprocessed_img, cls=False)

            # 각 결과에서 가장 높은 정확도를 가진 결과만 선택
            for line in ocr_result:
                text, confidence = line[1][0], line[1][1]
                if confidence > highest_confidence:
                    highest_confidence = confidence
                    best_result = {
                        'language': lang,
                        'text': text,
                        'confidence': confidence
                    }

        return best_result

    def run_ocr_on_image(self, image_path):
        """
        이미지 경로에서 이미지를 읽고 OCR 실행 후,
        가장 높은 정확도를 가진 결과를 반환.

        Args:
            image_path (str): OCR을 실행할 이미지 파일 경로.

        Returns:
            dict: 가장 높은 정확도의 OCR 결과.
        """
        img = cv2.imread(image_path)
        if img is not None:
            return self.run_ocr(img)
        else:
            return None

    def compare_images_ssim(self, img1, img2):
        """
        SSIM을 사용하여 두 이미지의 유사도를 계산.

        Args:
            img1 (numpy.ndarray): 첫 번째 이미지.
            img2 (numpy.ndarray): 두 번째 이미지.

        Returns:
            float: 두 이미지 간의 SSIM 유사도 (0~1, 1에 가까울수록 유사).
        """
        gray1 = cv2.cvtColor(img1, cv2.COLOR_BGR2GRAY)
        gray2 = cv2.cvtColor(img2, cv2.COLOR_BGR2GRAY)

        # SSIM을 통해 두 이미지 간의 구조적 유사도를 계산
        score, _ = ssim(gray1, gray2, full=True)
        return score

    def determine_document_type(self, image_path, vector_list, stored_images):
        """
        제공된 이미지의 제목 부분을 자르고 DB의 저장된 이미지들과 비교하여
        가장 유사한 문서 타입을 결정.

        Args:
            image_path (str): 처리할 이미지 파일 경로.
            vector_list (list): 제목 부분 벡터 리스트.
            stored_images (dict): DB에서 가져온 저장된 이미지들.

        Returns:
            str: 가장 유사한 문서 타입 (유사한 문서가 없으면 "Unknown" 반환).
        """
        image = cv2.imread(image_path)
        if image is None:
            return "Image not found"

        best_match = None
        highest_similarity = -1

        for vector in vector_list:
            # 제목 부분 벡터를 이용해 이미지 자르기
            cropped_image = crop_image_by_vector(image, vector)

            for doc_type, stored_image_path in stored_images.items():
                stored_image = cv2.imread(stored_image_path)  # DB에서 가져온 이미지 로드
                if stored_image is None:
                    continue

                # SSIM으로 유사도 비교 (1에 가까울수록 유사)
                similarity = self.compare_images_ssim(cropped_image, stored_image)
                if similarity > highest_similarity:
                    highest_similarity = similarity
                    best_match = doc_type

        return best_match if best_match else "Unknown"