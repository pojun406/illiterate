import logging
import sys
import os
import json
import cv2
import numpy as np
from PIL import Image
from MyFinalPPOCR import MyFinalPPOCR
from db_connection import get_title_vector, get_images_from_db, get_vectors_by_type, get_document_info
from image_preprocessing import crop_image_by_vector
from pathlib import Path
import locale

# 기본 로깅 설정
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# 로케일 설정 (한국어 UTF-8)
try:
    locale.setlocale(locale.LC_ALL, 'ko_KR.UTF-8')
except locale.Error:
    logger.warning("Warning: 'ko_KR.UTF-8' is not available. Setting to default locale.")
    locale.setlocale(locale.LC_ALL, '')

ocr = MyFinalPPOCR()
sys.stdout.reconfigure(encoding='utf-8')

def parse_vector(vector_str):
    numbers = [float(num) for num in vector_str.replace('(', '').replace(')', '').split(',')]
    return [(numbers[i], numbers[i+1]) for i in range(0, len(numbers), 2)]

def process_image(image_path):
    try:
        image_path = Path(image_path).resolve().as_posix()
        logger.info(f"Attempting to read image from: {image_path}")
        logger.debug(f"File exists: {os.path.exists(image_path)}")
        logger.debug(f"File size: {os.path.getsize(image_path) if os.path.exists(image_path) else 'N/A'}")

        results = []

        with Image.open(image_path) as img:
            img = img.convert('RGB')
            image = np.array(img)

        logger.info(f"Image shape: {image.shape}")
        logger.debug(f"Image dtype: {image.dtype}")

        if len(image.shape) == 2:
            image = cv2.cvtColor(image, cv2.COLOR_GRAY2BGR)
        elif image.shape[2] == 4:
            image = cv2.cvtColor(image, cv2.COLOR_RGBA2BGR)
        elif image.shape[2] != 3:
            raise ValueError(f"Unexpected image format: {image.shape}")

        logger.info("Getting title vectors from database...")
        title_vectors_dict = get_title_vector()
        logger.debug(f"Title vectors: {title_vectors_dict}")

        cropped_images = []

        logger.info("Processing title vectors...")
        for document_index, vectors in title_vectors_dict.items():
            logger.info(f"Processing document index: {document_index}")
            for vector in vectors:
                logger.debug(f"Processing vector: {vector}")
                if len(vector) == 4:
                    cropped_image = crop_image_by_vector(image, vector)
                    cropped_images.append((document_index, cropped_image))
                    logger.info(f"Cropped image added and saved for document index: {document_index}")
                else:
                    logger.warning(f"Invalid vector format for document_index {document_index}: {vector}")

        logger.info("Getting stored images from database...")
        stored_images = get_images_from_db()
        logger.debug(f"Stored images: {stored_images}")

        logger.info("Determining document type...")
        document_index = ocr.determine_document_type(image_path, title_vectors_dict, stored_images)
        logger.info(f"Determined document type: {document_index}")

        logger.info("Getting vectors for determined document type...")
        vectors = get_vectors_by_type(document_index)
        logger.debug(f"Vectors: {vectors}")

        logger.info("Getting document info...")
        document_index, img_info = get_document_info(document_index)
        if img_info:
            for vector_str, label in json.loads(img_info).items():
                logger.info(f"\nProcessing vector for {label}: {vector_str}")
                vector = parse_vector(vector_str)

                cropped_image = crop_image_by_vector(cv2.imdecode(np.fromfile(image_path, dtype=np.uint8), cv2.IMREAD_COLOR), vector)

                try:
                    ocr_result = ocr.run_ocr(cropped_image)
                    logger.debug(f"Raw OCR result for {label}: {json.dumps(ocr_result, ensure_ascii=False, indent=2)}")

                    if isinstance(ocr_result, dict) and 'error' in ocr_result:
                        processed_result = ""
                    elif isinstance(ocr_result, list) and len(ocr_result) > 0:
                        combined_text = ' '.join([r['text'] for r in ocr_result])
                        processed_result = combined_text
                    else:
                        processed_result = {"error": "Unexpected OCR result format", "raw_result": str(ocr_result)}

                    logger.info(f"Processed OCR result for {label}: {processed_result}")
                except Exception as e:
                    logger.error(f"Error in OCR processing for {label}: {str(e)}", exc_info=True)
                    processed_result = {"error": f"OCR processing failed: {str(e)}"}

                results.append({
                    "vector": vector_str,
                    "label": label,
                    "text": processed_result
                })

        final_result = {
            "document_index": document_index,
            "results": results
        }

        return json.dumps(final_result, ensure_ascii=False, indent=2)

    except Exception as e:
        logger.error(f"Error processing image: {e}", exc_info=True)
        return {"error": str(e)}

if __name__ == "__main__":
    if len(sys.argv) > 1:
        image_path = sys.argv[1]
        result = process_image(image_path)
        logger.info("OCR 처리 결과가 ocr_result.json 파일에 저장되었습니다.")
    else:
        logger.warning("이미지 경로가 제공되지 않았습니다.")
