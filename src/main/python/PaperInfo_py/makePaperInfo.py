import cv2
from tkinter import *
from PIL import Image, ImageTk
import json

def select_roi(image_path, json_name):
    """
    Tkinter를 사용하여 이미지에서 ROI를 선택하고 좌표를 JSON으로 저장하는 함수입니다.
    """
    # OpenCV로 이미지 로드
    image = cv2.imread(image_path)
    if image is None:
        print("Error: Unable to load image.")
        return None

    # OpenCV 이미지를 RGB로 변환하여 PIL 이미지로 변환
    image_rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    image_pil = Image.fromarray(image_rgb)

    # Tkinter 윈도우 생성
    root = Tk()
    root.title("Scrollable Image with ROI Selection")
    img_width, img_height = image_pil.size

    # Tkinter 캔버스와 스크롤바 추가
    canvas = Canvas(root, width=800, height=600)
    hbar = Scrollbar(root, orient=HORIZONTAL)
    hbar.pack(side=BOTTOM, fill=X)
    hbar.config(command=canvas.xview)
    vbar = Scrollbar(root, orient=VERTICAL)
    vbar.pack(side=RIGHT, fill=Y)
    vbar.config(command=canvas.yview)
    canvas.config(xscrollcommand=hbar.set, yscrollcommand=vbar.set)
    canvas.pack(side=LEFT, expand=True, fill=BOTH)

    # 이미지를 Tkinter 형식으로 변환 및 표시
    img_tk = ImageTk.PhotoImage(image_pil)
    canvas.create_image(0, 0, anchor="nw", image=img_tk)
    canvas.config(scrollregion=(0, 0, img_width, img_height))

    # ROI 선택 변수 및 데이터
    start_x, start_y = None, None
    rect = None
    roi_data = {}

    def on_mouse_down(event):
        nonlocal start_x, start_y, rect
        start_x = canvas.canvasx(event.x)
        start_y = canvas.canvasy(event.y)
        if rect:
            canvas.delete(rect)

    def on_mouse_drag(event):
        nonlocal rect
        cur_x = canvas.canvasx(event.x)
        cur_y = canvas.canvasy(event.y)
        if rect:
            canvas.delete(rect)
        rect = canvas.create_rectangle(start_x, start_y, cur_x, cur_y, outline='red')

    def on_mouse_up(event):
        nonlocal rect
        end_x = canvas.canvasx(event.x)
        end_y = canvas.canvasy(event.y)
        top_left = (start_x, start_y)
        bottom_right = (end_x, end_y)

        # ROI 이미지 표시
        roi = image[int(start_y):int(end_y), int(start_x):int(end_x)]
        cv2.imshow("Selected ROI", roi)
        cv2.waitKey(0)
        cv2.destroyAllWindows()

        def save_roi_info():
            description = text.get("1.0", "end-1c")
            roi_data[f"({top_left}, {bottom_right})"] = description
            input_window.destroy()

        # ROI 설명 창 생성
        input_window = Toplevel(root)
        input_window.title("ROI Description")
        Label(input_window, text="Enter a description for the selected ROI:").pack()
        text = Text(input_window, height=5, width=40)
        text.pack()
        Button(input_window, text="Save", command=save_roi_info).pack()

    canvas.bind("<ButtonPress-1>", on_mouse_down)
    canvas.bind("<B1-Motion>", on_mouse_drag)
    canvas.bind("<ButtonRelease-1>", on_mouse_up)

    def save_and_exit():
        if roi_data:
            with open(json_name, 'w', encoding='utf-8') as json_file:
                json.dump(roi_data, json_file, ensure_ascii=False, indent=4)
            print(f"ROI data saved to {json_name}")
        root.quit()

    Button(root, text="Save and Exit", command=save_and_exit).pack(side=BOTTOM)
    root.mainloop()

    # 선택된 "제목" 벡터 반환
    return [k for k, v in roi_data.items() if v == "제목"]