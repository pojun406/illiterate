import requests

# Spring Boot API URL
url = "http://localhost:8080/api/vectors/12345"  # 12345는 imageId

# API 요청
response = requests.get(url)

# 요청 성공 시 JSON 데이터 출력
if response.status_code == 200:
    vector_data = response.json()
    print("벡터 데이터:", vector_data)
else:
    print(f"Error: {response.status_code}")