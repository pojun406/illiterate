import cv2
import numpy as np
import json
import os

# 이미지를 비율에 맞게 확대/축소하는 함수
def resize_image(image, scale=1.0):
    """이미지를 비율에 맞게 확대/축소하는 함수"""
    width = int(image.shape[1] * scale)
    height = int(image.shape[0] * scale)
    resized_image = cv2.resize(image, (width, height), interpolation=cv2.INTER_LINEAR)
    return resized_image

# 이미지를 선명하게 처리하는 함수
def enhance_image(image):
    """이미지를 선명하게 처리하는 함수"""
    denoised_image = cv2.fastNlMeansDenoisingColored(image, None, 10, 10, 7, 21)
    enhanced_image = cv2.detailEnhance(denoised_image, sigma_s=10, sigma_r=0.15)
    lab = cv2.cvtColor(enhanced_image, cv2.COLOR_BGR2LAB)
    l, a, b = cv2.split(lab)
    clahe = cv2.createCLAHE(clipLimit=3.0, tileGridSize=(8, 8))
    cl = clahe.apply(l)
    limg = cv2.merge((cl, a, b))
    enhanced_image = cv2.cvtColor(limg, cv2.COLOR_LAB2BGR)
    return enhanced_image

# 이미지 자르기 및 저장 함수
def crop_image_by_vector(image, vector):
    """이미지의 특정 벡터 영역을 잘라냄"""
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

# 벡터 데이터를 JSON으로 받아 처리하는 함수
def get_vector_data_from_json(json_data):
    try:
        vector_data = json.loads(json_data)
        vector_list = [
            (
                tuple(vector["top_left"]),
                tuple(vector["top_right"]),
                tuple(vector["bottom_left"]),
                tuple(vector["bottom_right"])
            )
            for vector in vector_data
        ]
        return vector_list
    except json.JSONDecodeError as e:
        print(f"Error decoding JSON: {e}")
        return []
