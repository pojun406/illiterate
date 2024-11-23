import cv2
import json
from tkinter import Tk, Label, Entry, Button

def get_description():
    """
    설명을 입력받기 위한 tkinter 창을 띄우고,
    사용자가 입력을 완료할 때까지 대기.
    """
    def submit():
        nonlocal description
        description = entry.get()
        root.quit()  # 창 닫기

    root = Tk()
    root.title("Enter ROI Description")
    Label(root, text="Enter description for selected ROI:").pack()
    entry = Entry(root, width=50)
    entry.pack()
    Button(root, text="Submit", command=submit).pack()

    description = ""
    root.mainloop()
    root.destroy()
    return description


def select_rois_with_descriptions(image_path, output_file="roi_descriptions.json", window_width=800, window_height=600):
    """
    ROI를 선택하고 설명을 입력받아 JSON 형식으로 저장.
    이미지 창 크기를 조정하여 ROI 선택이 가능하도록 설정.
    """
    # 이미지 로드
    image = cv2.imread(image_path)
    if image is None:
        raise ValueError("Image not found or unable to load.")

    original_height, original_width = image.shape[:2]

    # 이미지 리사이즈 비율 계산
    scale_width = window_width / original_width
    scale_height = window_height / original_height
    scale = min(scale_width, scale_height)

    # 리사이즈된 이미지 (보간법 사용)
    resized_image = cv2.resize(
        image,
        (int(original_width * scale), int(original_height * scale)),
        interpolation=cv2.INTER_AREA  # 고품질 축소 보간법
    )

    roi_data = {}

    while True:
        # ROI 선택 (리사이즈된 이미지 기준)
        x, y, w, h = cv2.selectROI("Select ROI (Press ESC to exit)", resized_image, False, False)
        if w == 0 or h == 0:  # 아무 영역도 선택하지 않으면 루프 종료
            break

        # 선택된 좌표를 원본 이미지 기준으로 변환
        x1 = int(x / scale)
        y1 = int(y / scale)
        x2 = int((x + w) / scale)
        y2 = int((y + h) / scale)
        vector_value = f"(({x1}, {y1}), ({x2}, {y2}), ({x1}, {y2}), ({x2}, {y2}))"

        # 설명 입력 창 호출
        description = get_description()

        # "벡터값": "설명" 형식으로 저장
        if description:
            roi_data[vector_value] = description
            print(f"Saved ROI '{vector_value}' with description '{description}'")

    # 결과를 JSON 파일로 저장
    with open(output_file, "w", encoding="utf-8") as f:
        json.dump(roi_data, f, ensure_ascii=False, indent=4)

    print(f"ROI data saved to {output_file}")
    return roi_data
