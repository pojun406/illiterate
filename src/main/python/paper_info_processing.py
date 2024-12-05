import cv2
import json
import tkinter as tk
from tkinter import Toplevel, Canvas, Scrollbar, Label, Entry, Button
from PIL import Image, ImageTk

def select_rois_with_descriptions(image_path, output_file="roi_descriptions.json"):
    """
    원본 크기의 이미지를 사용하며, tkinter 스크롤 캔버스에서 ROI를 선택하고
    JSON 파일로 저장하는 함수.
    """
    # Load the image
    image = cv2.imread(image_path)
    if image is None:
        raise ValueError("Image not found or unable to load.")

    # Convert image to RGB for tkinter compatibility
    image_rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    image_pil = Image.fromarray(image_rgb)
    image_width, image_height = image_pil.size

    # Initialize tkinter root window
    root = tk.Tk()
    root.title("Select ROI")

    # Create a scrollable canvas
    canvas_frame = Toplevel(root)
    canvas = Canvas(canvas_frame, width=1800, height=800)
    scrollbar_x = Scrollbar(canvas_frame, orient="horizontal", command=canvas.xview)
    scrollbar_y = Scrollbar(canvas_frame, orient="vertical", command=canvas.yview)
    canvas.configure(xscrollcommand=scrollbar_x.set, yscrollcommand=scrollbar_y.set)

    # Add the image to the canvas
    img_tk = ImageTk.PhotoImage(image_pil)
    canvas.create_image(0, 0, anchor="nw", image=img_tk)
    canvas.config(scrollregion=(0, 0, image_width, image_height))

    canvas.grid(row=0, column=0, sticky="nsew")
    scrollbar_x.grid(row=1, column=0, sticky="ew")
    scrollbar_y.grid(row=0, column=1, sticky="ns")

    # Store ROI data
    roi_data = {}

    def get_description():
        """
        사용자로부터 설명을 입력받음.
        """
        desc_root = Toplevel(root)
        desc_root.title("Enter ROI Description")

        desc_label = Label(desc_root, text="Enter description for selected ROI:")
        desc_label.pack()

        desc_entry = Entry(desc_root, width=50)
        desc_entry.pack()

        def submit():
            nonlocal description
            description = desc_entry.get()
            desc_root.destroy()

        submit_button = Button(desc_root, text="Submit", command=submit)
        submit_button.pack()

        description = ""
        desc_root.wait_window()  # 설명 창이 닫힐 때까지 대기
        return description

    def on_click(event):
        # Store the starting coordinates of the ROI
        canvas.start_x = canvas.canvasx(event.x)
        canvas.start_y = canvas.canvasy(event.y)
        # Create a rectangle for visual feedback
        canvas.rect_id = canvas.create_rectangle(
            canvas.start_x, canvas.start_y, canvas.start_x, canvas.start_y,
            outline="blue", width=3
        )

    def on_drag(event):
        # Update the rectangle's end coordinates as the mouse is dragged
        end_x = canvas.canvasx(event.x)
        end_y = canvas.canvasy(event.y)
        canvas.coords(canvas.rect_id, canvas.start_x, canvas.start_y, end_x, end_y)

    def on_release(event):
        # Store the ending coordinates of the ROI
        end_x = canvas.canvasx(event.x)
        end_y = canvas.canvasy(event.y)

        # Ensure coordinates are valid
        x1, y1 = int(min(canvas.start_x, end_x)), int(min(canvas.start_y, end_y))
        x2, y2 = int(max(canvas.start_x, end_x)), int(max(canvas.start_y, end_y))

        if x2 - x1 <= 0 or y2 - y1 <= 0:
            print("Invalid ROI selected. Skipping...")
            return

        vector_value = f"(({x1}, {y1}), ({x2}, {y1}), ({x1}, {y2}), ({x2}, {y2}))"

        # Prevent duplicate ROI
        if vector_value in roi_data:
            print(f"Duplicate ROI detected: {vector_value}. Skipping...")
            return

        # Get description immediately
        description = get_description()
        if not description.strip():
            print(f"Skipping ROI with empty description for coordinates: {vector_value}")
            canvas.delete(canvas.rect_id)  # Remove the rectangle
            return
        if description in roi_data.values():
            print(f"Duplicate description detected: '{description}'. Skipping...")
            canvas.delete(canvas.rect_id)  # Remove the rectangle
            return

        # Save ROI
        roi_data[vector_value] = description
        print(f"Saved ROI '{vector_value}' with description '{description}'")

    # Bind mouse events for ROI selection
    canvas.bind("<ButtonPress-1>", on_click)
    canvas.bind("<B1-Motion>", on_drag)
    canvas.bind("<ButtonRelease-1>", on_release)

    # Run tkinter main loop
    root.mainloop()

    # Save ROI data to JSON file
    with open(output_file, "w", encoding="utf-8") as f:
        json.dump(roi_data, f, ensure_ascii=False, indent=4)

    print(f"ROI data saved to {output_file}")
    return roi_data
