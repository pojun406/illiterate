from letsgopaddle import MyPaddleOCR

ocr = MyPaddleOCR()

img_path = 'C:/Users/404ST011/PycharmProjects/pythonProject/photo/456.jpg'
result = ocr.run_ocr(img_path, debug=True)

print(result)