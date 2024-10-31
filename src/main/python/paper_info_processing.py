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

def select_rois_with_descriptions(image_path):
    # 이미지 로드
    image = cv2.imread(image_path)
    if image is None:
        raise ValueError("Image not found or unable to load.")

    roi_data = {}

    # ROI 선택 반복문
    while True:
        # OpenCV로 ROI 선택
        x, y, w, h = cv2.selectROI("Select ROI (Press ESC to exit)", image, False, False)
        if w == 0 or h == 0:  # 아무 영역도 선택하지 않으면 루프 종료
            break

        # 선택된 좌표
        x1, y1 = x, y
        x2, y2 = x + w, y
        x3, y3 = x, y + h
        x4, y4 = x + w, y + h
        vector_value = f"(({x1}, {y1}), ({x2}, {y2}), ({x3}, {y3}), ({x4}, {y4}))"

        # 설명 입력 창 호출
        description = get_description()

        # "벡터값": "설명" 형식으로 저장
        if description:
            roi_data[vector_value] = description
            print(f"Saved ROI '{vector_value}' with description '{description}'")


    return roi_data
