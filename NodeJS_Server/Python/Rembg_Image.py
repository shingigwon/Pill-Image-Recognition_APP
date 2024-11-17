from rembg import remove
import sys
import os

def remove_background(input_path):
    # 이미지 파일인지 확인
    valid_extensions = ['.jpg', '.jpeg', '.png', '.bmp']
    _, ext = os.path.splitext(input_path)
    if ext.lower() not in valid_extensions:
        return

    # 배경 제거
    with open(input_path, "rb") as input_file:
        input_data = input_file.read()  # 파일 데이터를 읽음
        output_data = remove(input_data)  # 배경 제거

        with open(input_path, "wb") as output_file:
            output_file.write(output_data)  # 배경이 제거된 데이터를 파일로 저장
        

if __name__ == "__main__":
    remove_background(sys.argv[1])
