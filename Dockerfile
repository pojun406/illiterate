FROM continuumio/anaconda3
WORKDIR /app
COPY . .
RUN pip install -r requirements.txt
CMD ["python", "api_server.py"]