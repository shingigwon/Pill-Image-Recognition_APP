import sys
import os
# import json
from google.cloud import vision

sys.stdout.reconfigure(encoding='utf-8')
# # Set environment variable
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "Python\OCR\ocr-project-app-5cb3803db6d1.json"
def detect_text(path):
    # arr=[]
    ocr_txt = ""

    """Detects text in the file."""
    client = vision.ImageAnnotatorClient()

    with open(path, "rb") as image_file:
        content = image_file.read()

    image = vision.Image(content=content)

    text_response = client.text_detection(image=image)
    texts = text_response.text_annotations


    for text in texts[1:]:  # Exclude the first text (full text of the image)
        # print(f'"{text.description}"')
        # arr.append(text.description)
        ocr_txt+=text.description

    if text_response.error.message:
        raise Exception(
            "{}\nFor more info on error messages, check: "
            "https://cloud.google.com/apis/design/errors".format(text_response.error.message)
        )  
    
    # print(json.dumps(arr))
    # return json.dumps(arr)
    return ocr_txt
        

if __name__ == '__main__':
    print(detect_text("uploads/2/201207453.jpg"))

