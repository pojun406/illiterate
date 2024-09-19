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
    lab = cv2.cvtColor(enhanced_image, cv2.COLOR_BGR2LAB)
    l, a, b = cv2.split(lab)
    clahe = cv2.createCLAHE(clipLimit=3.0, tileGridSize=(8, 8))
    cl = clahe.apply(l)
    limg = cv2.merge((cl, a, b))
    return cv2.cvtColor(limg, cv2.COLOR_LAB2BGR)


def crop_image_by_vector(image, vector):
    """
    주어진 벡터 정보에 따라 이미지를 잘라냅니다.

    Args:
        image (numpy.ndarray): 원본 이미지 배열.
        vector (tuple): 잘라낼 이미지의 좌표 정보 (top_left, top_right, bottom_left, bottom_right).

    Returns:
        numpy.ndarray: 잘라낸 이미지 배열.
    """
    (top_left, top_right, bottom_left, bottom_right) = vector
    x1, y1 = top_left
    x2, y2 = top_right
    x3, y3 = bottom_left
    x4, y4 = bottom_right
    start_x = int(min(x1, x3))
    start_y = int(min(y1, y3))
    end_x = int(max(x2, x4))
    end_y = int(max(y2, y4))
    cropped_image = image[start_y:end_y, start_x:end_x]
    return cropped_image