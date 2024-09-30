import cv2
import numpy as np


def resize_image(image, scale=1.0):
    """
    이미지 크기를 조정합니다.

    Args:
        image (numpy.ndarray): 원본 이미지 배열.
        scale (float): 이미지 크기 조정 비율.

    Returns:
        numpy.ndarray: 크기가 조정된 이미지 배열.
    """
    width = int(image.shape[1] * scale)
    height = int(image.shape[0] * scale)
    return cv2.resize(image, (width, height), interpolation=cv2.INTER_LINEAR)


def enhance_image(image):
    """
    이미지의 선명도를 개선합니다.

    Args:
        image (numpy.ndarray): 원본 이미지 배열.

    Returns:
        numpy.ndarray: 선명도가 개선된 이미지 배열.
    """
    denoised_image = cv2.fastNlMeansDenoisingColored(image, None, 10, 10, 7, 21)
    enhanced_image = cv2.detailEnhance(denoised_image, sigma_s=10, sigma_r=0.15)

    # 그레이스케일로 변환
    gray = cv2.cvtColor(enhanced_image, cv2.COLOR_BGR2GRAY)

    # CLAHE 적용
    clahe = cv2.createCLAHE(clipLimit=3.0, tileGridSize=(8, 8))
    cl = clahe.apply(gray)

    # 다시 BGR로 변환
    return cv2.cvtColor(cl, cv2.COLOR_GRAY2BGR)


def crop_image_by_vector(image, vector):
    """
    주어진 벡터 정보에 따라 이미지를 잘라냅니다.

    Args:
        image (numpy.ndarray): 원본 이미지 배열.
        vector (tuple): 잘라낼 이미지의 좌표 정보 (top_left, top_right, bottom_left, bottom_right).

    Returns:
        numpy.ndarray: 잘라낸 이미지 배열.
    """
    try:
        (top_left, top_right, bottom_left, bottom_right) = vector
        x1, y1 = map(int, top_left)
        x2, y2 = map(int, top_right)
        x3, y3 = map(int, bottom_left)
        x4, y4 = map(int, bottom_right)
        start_x = max(0, min(x1, x3))
        start_y = max(0, min(y1, y3))
        end_x = min(image.shape[1], max(x2, x4))
        end_y = min(image.shape[0], max(y2, y4))
        if start_x >= end_x or start_y >= end_y:
            raise ValueError(f"Invalid crop coordinates: start_x={start_x}, end_x={end_x}, start_y={start_y}, end_y={end_y}")
        cropped_image = image[start_y:end_y, start_x:end_x]
        if cropped_image.size == 0:
            raise ValueError(f"Cropped image is empty: shape={cropped_image.shape}")
        return cropped_image
    except Exception as e:
        print(f"Error in crop_image_by_vector: {str(e)}")
        print(f"Vector: {vector}")
        print(f"Image shape: {image.shape}")
        raise