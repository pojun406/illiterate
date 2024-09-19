import os

import numpy as np

from MyFinalPPOCR import MyFinalPPOCR  # MyFinalPPOCR로 변경됨

def download_ocr_models():
    """
    PaddleOCR 모델을 다운로드하는 함수.
    여러 언어의 OCR 모델을 미리 다운로드.
    """
    # PaddleOCR 인스턴스 생성 (모델 파일 다운로드 트리거)
    ocr = MyFinalPPOCR()

    # 한국어, 중국어, 영어 모델 다운로드 트리거
    languages = ['korean', 'chinese', 'english']

    for lang in languages:
        print(f"{lang} 모델 다운로드 중...")
        # 언어별로 OCR을 실행하여 해당 언어 모델을 다운로드
        ocr_model = ocr.ocr_models[lang]
        dummy_result = ocr_model.ocr(np.zeros((100, 100, 3), dtype=np.uint8), cls=False)
        print(f"{lang} 모델 다운로드 완료")

    print("모든 OCR 모델이 다운로드되었습니다.")

if __name__ == '__main__':
    download_ocr_models()