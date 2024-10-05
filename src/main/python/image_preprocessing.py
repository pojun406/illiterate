import cv2
import numpy as np


def resize_image(image, scale=1.0):
    """
    주어진 이미지를 설정된 비율로 크기를 조정하는 함수.

    Args:
        image (numpy.ndarray): 원본 이미지 배열.
        scale (float): 이미지 크기를 조정할 비율 (기본값 1.0).

    Returns:
        numpy.ndarray: 크기가 조정된 이미지 배열.
    """
    width = int(image.shape[1] * scale)  # 새 이미지의 너비 계산
    height = int(image.shape[0] * scale)  # 새 이미지의 높이 계산
    return cv2.resize(image, (width, height), interpolation=cv2.INTER_LINEAR)  # 지정된 비율로 이미지 크기 조정


def enhance_image(image):
    """
    이미지의 선명도를 향상시키는 함수. 노이즈 제거와 대비 개선을 통해 이미지 품질을 개선.

    Args:
        image (numpy.ndarray): 원본 이미지 배열.

    Returns:
        numpy.ndarray: 선명도가 개선된 이미지 배열.
    """
    # 이미지에서 색 노이즈 제거 (색상에 따른 잡음 제거)
    denoised_image = cv2.fastNlMeansDenoisingColored(image, None, 10, 10, 7, 21)

    # 이미지의 디테일을 강조하는 필터 적용 (시그마 값으로 디테일 정도 조정)
    enhanced_image = cv2.detailEnhance(denoised_image, sigma_s=10, sigma_r=0.15)

    # 이미지를 그레이스케일로 변환하여 대비를 더 잘 조정할 수 있게 처리
    gray = cv2.cvtColor(enhanced_image, cv2.COLOR_BGR2GRAY)

    # CLAHE (Contrast Limited Adaptive Histogram Equalization) 적용으로 대비를 향상시킴
    clahe = cv2.createCLAHE(clipLimit=3.0, tileGridSize=(8, 8))
    cl = clahe.apply(gray)

    # 이미지를 다시 BGR로 변환 (그레이스케일에서 색상을 복원)
    return cv2.cvtColor(cl, cv2.COLOR_GRAY2BGR)


def crop_image_by_vector(image, vector):
    """
    주어진 벡터 좌표에 따라 이미지를 잘라내는 함수.

    Args:
        image (numpy.ndarray): 원본 이미지 배열.
        vector (tuple): 잘라낼 이미지의 네 개 좌표를 나타내는 튜플
                        (top_left, top_right, bottom_left, bottom_right).

    Returns:
        numpy.ndarray: 잘라낸 이미지 배열.
    """
    try:
        # 벡터에서 좌표 값을 각각 추출
        (top_left, top_right, bottom_left, bottom_right) = vector
        x1, y1 = map(int, top_left)  # 좌상단 좌표
        x2, y2 = map(int, top_right)  # 우상단 좌표
        x3, y3 = map(int, bottom_left)  # 좌하단 좌표
        x4, y4 = map(int, bottom_right)  # 우하단 좌표

        # 이미지 자를 시작점 및 끝점을 결정 (네 좌표에서 최댓값, 최솟값 사용)
        start_x = max(0, min(x1, x3))  # 좌측에서 가장 작은 x 좌표
        start_y = max(0, min(y1, y3))  # 상단에서 가장 작은 y 좌표
        end_x = min(image.shape[1], max(x2, x4))  # 우측에서 가장 큰 x 좌표
        end_y = min(image.shape[0], max(y2, y4))  # 하단에서 가장 큰 y 좌표

        # 잘라낼 영역이 유효하지 않을 경우 오류 발생
        if start_x >= end_x or start_y >= end_y:
            raise ValueError(f"Invalid crop coordinates: start_x={start_x}, end_x={end_x}, start_y={start_y}, end_y={end_y}")

        # 이미지 자르기
        cropped_image = image[start_y:end_y, start_x:end_x]

        # 잘라낸 이미지가 비어 있는 경우 오류 발생
        if cropped_image.size == 0:
            raise ValueError(f"Cropped image is empty: shape={cropped_image.shape}")

        return cropped_image  # 잘라낸 이미지 반환
    except Exception as e:
        # 예외 발생 시 오류 메시지 출력 및 재발생
        print(f"Error in crop_image_by_vector: {str(e)}")
        print(f"Vector: {vector}")  # 잘라내기 시도한 벡터 출력
        print(f"Image shape: {image.shape}")  # 원본 이미지의 크기 출력
        raise  # 예외 다시 발생
