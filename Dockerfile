# 1. Ubuntu 베이스 이미지 사용
FROM ubuntu:20.04

# 2. 환경 변수 설정 (비대화형 설치 방지 및 로케일 설정)
ENV DEBIAN_FRONTEND=noninteractive \
    LANG=ko_KR.UTF-8 \
    LANGUAGE=ko_KR:ko \
    LC_ALL=ko_KR.UTF-8 \
    IMAGE_PATH="/app/image/"

# 3. 필요한 시스템 패키지 설치 (libGL, libgomp, 로케일 포함)
RUN apt-get update && \
    apt-get install -y wget bzip2 curl libgl1-mesa-glx libgomp1 locales && \
    echo "ko_KR.UTF-8 UTF-8" >> /etc/locale.gen && \
    locale-gen ko_KR.UTF-8 && \
    update-locale LANG=ko_KR.UTF-8 && \
    apt-get clean

# 4. 아나콘다 설치 스크립트 다운로드 및 설치
RUN wget https://repo.anaconda.com/archive/Anaconda3-2023.03-Linux-x86_64.sh -O anaconda.sh && \
    bash anaconda.sh -b -p /opt/conda && \
    rm anaconda.sh

# 5. 아나콘다 경로를 환경 변수에 추가
ENV PATH="/opt/conda/bin:$PATH"

# 6. 작업 디렉토리 설정 (컨테이너 내부 경로)
WORKDIR /app

# 7. 이미지 저장 디렉토리 생성
RUN mkdir -p /app/image

# 8. 로컬 파일을 도커 이미지로 복사
COPY ./src/main/python /app

# 9. 필요한 파이썬 패키지 설치 (pip 사용)
RUN pip install --no-cache-dir -r /app/requirements.txt

# 10. 서버 실행 (api_server.py 경로 지정)
CMD ["python", "/app/api_server.py"]
