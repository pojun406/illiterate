import cv2
from tkinter import *
from PIL import Image, ImageTk
import json

# 이미지 파일 경로 설정
image_path = 'C:/Users/byung/Desktop/img/22.png'

# OpenCV로 이미지 로드
image = cv2.imread(image_path)

if image is None:
    print("Error: Unable to load image. Check the file path.")
else:
    # OpenCV 이미지를 RGB로 변환하여 PIL 이미지로 변환
    image_rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    image_pil = Image.fromarray(image_rgb)

    # Tkinter 윈도우 생성
    root = Tk()
    root.title("Scrollable Image with ROI Selection")

    # 이미지 크기 얻기
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

    # 이미지를 Tkinter 형식으로 변환
    img_tk = ImageTk.PhotoImage(image_pil)
    canvas.create_image(0, 0, anchor="nw", image=img_tk)
    canvas.config(scrollregion=(0, 0, img_width, img_height))

    # ROI 선택을 위한 변수 초기화
    start_x, start_y = None, None
    rect = None
    roi_data = {}

    # 마우스 클릭 이벤트 처리 함수
    def on_mouse_down(event):
        global start_x, start_y, rect
        # 마우스 클릭 위치 저장
        start_x = canvas.canvasx(event.x)
        start_y = canvas.canvasy(event.y)
        # 이전 사각형이 있다면 삭제
        if rect:
            canvas.delete(rect)

    # 마우스 드래그 이벤트 처리 함수
    def on_mouse_drag(event):
        global rect
        # 드래그 중인 마우스 좌표
        cur_x = canvas.canvasx(event.x)
        cur_y = canvas.canvasy(event.y)

        # 이전 사각형 삭제
        if rect:
            canvas.delete(rect)

        # 새로운 사각형 그리기
        rect = canvas.create_rectangle(start_x, start_y, cur_x, cur_y, outline='red')

    # 마우스 버튼을 놓으면 ROI 좌표 출력 및 네 꼭짓점 계산
    def on_mouse_up(event):
        global rect
        end_x = canvas.canvasx(event.x)
        end_y = canvas.canvasy(event.y)

        # 네 꼭짓점 좌표 계산
        top_left = (start_x, start_y)
        top_right = (end_x, start_y)
        bottom_left = (start_x, end_y)
        bottom_right = (end_x, end_y)

        # 좌표 출력
        print(f"Top-left: {top_left}")
        print(f"Top-right: {top_right}")
        print(f"Bottom-left: {bottom_left}")
        print(f"Bottom-right: {bottom_right}")
        print("-----------------------------------")

        # ROI 선택한 부분 자르기
        roi = image[int(start_y):int(end_y), int(start_x):int(end_x)]
        cv2.imshow("Selected ROI", roi)
        cv2.waitKey(0)
        cv2.destroyAllWindows()

        # 사용자 입력을 받아 설명 추가
        def save_roi_info():
            description = text.get("1.0", "end-1c")  # Text 위젯에서 내용 가져오기
            # 네 꼭짓점 좌표와 설명을 roi_data에 저장
            roi_data[f"({top_left}, {top_right}, {bottom_left}, {bottom_right})"] = description
            print(f"Saved: {roi_data}")

            # 창 닫기
            input_window.destroy()

        # ROI 설명을 위한 작은 창 생성
        input_window = Toplevel(root)
        input_window.title("ROI Description")
        label = Label(input_window, text="Enter a description for the selected ROI:")
        label.pack()

        # 텍스트 박스 생성
        text = Text(input_window, height=5, width=40)
        text.pack()

        # 저장 버튼
        save_button = Button(input_window, text="Save", command=save_roi_info)
        save_button.pack()

    # 마우스 이벤트 바인딩
    canvas.bind("<ButtonPress-1>", on_mouse_down)
    canvas.bind("<B1-Motion>", on_mouse_drag)
    canvas.bind("<ButtonRelease-1>", on_mouse_up)

    # JSON 저장 및 프로그램 종료 함수
    def save_and_exit():
        if roi_data:
            with open('roi_data.json', 'w', encoding='utf-8') as json_file:
                json.dump(roi_data, json_file, ensure_ascii=False, indent=4)
            print("ROI data saved to roi_data.json")
        else:
            print("No ROI data to save")
        root.quit()  # 프로그램 종료

    # JSON 저장 및 종료 버튼 추가
    save_button = Button(root, text="Save to JSON and Exit", command=save_and_exit)
    save_button.pack(side=BOTTOM)

    root.mainloop()
