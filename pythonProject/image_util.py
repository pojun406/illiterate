import cv2
import numpy as np
import platform
from PIL import ImageFont, ImageDraw, Image
from matplotlib import pyplot as plt


def plt_imshow(title='image', img=None, figsize=(16, 12), dpi=2400):
    """Matplotlib을 이용하여 이미지를 고해상도로 화면에 표시하는 함수"""
    # 고해상도 화면을 위한 figure 설정
    plt.figure(figsize=figsize, dpi=dpi)

    # 이미지 파일 경로가 주어진 경우 이미지를 로드
    if isinstance(img, str):
        img = cv2.imread(img)

    # 여러 이미지를 동시에 보여주기 위한 처리
    if isinstance(img, list):
        # 타이틀이 리스트로 주어진 경우, 각 이미지에 대응하는 타이틀 사용
        if isinstance(title, list):
            titles = title
        else:
            titles = [title] * len(img)

        # 각 이미지를 서브플롯에 표시
        for i in range(len(img)):
            if len(img[i].shape) <= 2:
                rgb_img = cv2.cvtColor(img[i], cv2.COLOR_GRAY2RGB)
            else:
                rgb_img = cv2.cvtColor(img[i], cv2.COLOR_BGR2RGB)

            plt.subplot(1, len(img), i + 1)
            plt.imshow(rgb_img)
            plt.title(titles[i])
            plt.xticks([]), plt.yticks([])

        plt.show()
    else:
        # 단일 이미지를 화면에 표시
        if len(img.shape) < 3:
            rgb_img = cv2.cvtColor(img, cv2.COLOR_GRAY2RGB)
        else:
            rgb_img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)

        plt.imshow(rgb_img)
        plt.title(title)
        plt.xticks([]), plt.yticks([])
        plt.show()


def put_text(image, text, x, y, color=(0, 255, 0), font_size=22):
    """이미지에 텍스트를 추가하는 함수"""
    # 이미지가 numpy 배열인 경우 PIL 이미지로 변환
    if isinstance(image, np.ndarray):
        color_converted = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
        image = Image.fromarray(color_converted)

    # 운영체제에 따라 폰트 설정
    if platform.system() == 'Darwin':
        font = 'AppleGothic.ttf'
    elif platform.system() == 'Windows':
        font = 'malgun.ttf'

    # 텍스트 폰트와 그리기 설정
    image_font = ImageFont.truetype(font, font_size)
    draw = ImageDraw.Draw(image)
    draw.text((x, y), text, font=image_font, fill=color)

    # PIL 이미지를 다시 numpy 배열로 변환
    numpy_image = np.array(image)
    opencv_image = cv2.cvtColor(numpy_image, cv2.COLOR_RGB2BGR)

    return opencv_image


def resize_image(image, scale=2.0):
    """이미지를 확대하는 함수"""
    if scale <= 0:
        raise ValueError("Scale must be greater than 0")

    # 이미지의 크기를 계산하여 확대/축소
    height, width = image.shape[:2]
    new_size = (int(width * scale), int(height * scale))
    resized_image = cv2.resize(image, new_size, interpolation=cv2.INTER_CUBIC)
    return resized_image


def enhance_image(image):
    """이미지의 선명도를 향상시키는 함수"""
    # 노이즈 감소
    denoised_image = cv2.fastNlMeansDenoisingColored(image, None, 10, 10, 7, 21)

    # 이미지 세부 사항 강조
    enhanced_image = cv2.detailEnhance(denoised_image, sigma_s=10, sigma_r=0.15)

    # LAB 컬러 공간으로 변환하여 밝기 채널을 조정
    lab = cv2.cvtColor(enhanced_image, cv2.COLOR_BGR2LAB)
    l, a, b = cv2.split(lab)
    clahe = cv2.createCLAHE(clipLimit=3.0, tileGridSize=(8, 8))
    cl = clahe.apply(l)
    limg = cv2.merge((cl, a, b))
    enhanced_image = cv2.cvtColor(limg, cv2.COLOR_LAB2BGR)

    return enhanced_image


def binarize_image(image, method='global', block_size=11, C=2):
    """이미지 이진화 함수"""
    # 그레이스케일로 변환
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    # 이진화 방법에 따라 처리
    if method == 'global':
        # 전역 임계값 이진화 (Otsu's Method)
        _, binary_image = cv2.threshold(gray, 128, 255, cv2.THRESH_BINARY | cv2.THRESH_OTSU)
    elif method == 'adaptive_mean':
        # 적응형 임계값 이진화 (평균 기반)
        binary_image = cv2.adaptiveThreshold(gray, 255, cv2.ADAPTIVE_THRESH_MEAN_C, cv2.THRESH_BINARY, block_size, C)
    elif method == 'adaptive_gaussian':
        # 적응형 임계값 이진화 (가우시안 기반)
        binary_image = cv2.adaptiveThreshold(gray, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY, block_size,
                                             C)
    else:
        raise ValueError("Invalid binarization method. Choose from 'global', 'adaptive_mean', or 'adaptive_gaussian'.")

    return binary_image


def run1():
    # 이미지 경로 설정
    image_path = "path/to/your/image.jpg"

    # 이미지 불러오기
    image = cv2.imread(image_path)

    # 이진화된 이미지
    binary_image = binarize_image(image, method='adaptive_gaussian')

    # 선명도 향상된 이미지
    enhanced_image = enhance_image(image)

    # 확대된 이미지
    resized_image = resize_image(image, scale=2.0)

    # 이미지에 텍스트 추가
    text_image = put_text(image, "Sample Text", 50, 50)

    # 결과 이미지들 표시
    plt_imshow(["Original", "Binary", "Enhanced", "Resized", "Text"],
               [image, binary_image, enhanced_image, resized_image, text_image])


# 테스트 코드
if __name__ == "__main__":
    image_path = "path/to/your/image.jpg"  # 이미지 경로를 입력하세요
    image = cv2.imread(image_path)

    # 이진화된 이미지
    binary_image = binarize_image(image, method='adaptive_gaussian')

    # 선명도 향상된 이미지
    enhanced_image = enhance_image(image)

    # 확대된 이미지
    resized_image = resize_image(image, scale=2.0)

    # 이미지에 텍스트 추가
    text_image = put_text(image, "Sample Text", 50, 50)

    # 결과 이미지들 표시
    plt_imshow(["Original", "Binary", "Enhanced", "Resized", "Text"],
               [image, binary_image, enhanced_image, resized_image, text_image])