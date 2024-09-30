import json
import mysql.connector
import os


def get_database_connection():
    """
    MySQL 데이터베이스 연결을 생성합니다.

    Returns:
        mysql.connector.connection_cext.CMySQLConnection: 데이터베이스 연결 객체
    """
    connection = mysql.connector.connect(
        host='0.0.0.0',
        database='illiterate',
        user='root',
        password='REDACTED_PASSWORD'
    )
    return connection


def get_title_vector():
    """
    paper_info 테이블에서 title_vector를 가져옵니다.

    Returns:
        list: title_vector 정보
    """
    conn = get_database_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT title_vector FROM paper_info WHERE document_index=1")
    vector = cursor.fetchone()
    conn.close()

    if vector and vector[0]:
        try:
            # 문자열로 저장된 튜플을 실제 튜플로 변환
            title_vector = eval(vector[0])
            print(f"Title vector: {title_vector}")  # 디버깅을 위한 출력
            return [title_vector]  # 리스트로 감싸서 반환
        except Exception as e:
            print(f"Error processing vector: {e}")
            print(f"Original data: {vector[0]}")  # 원본 데이터 출력
            return []
    else:
        print("No data found or empty string")  # 디버깅을 위한 출력
        return []


def get_images_from_db():
    """
    paper_info 테이블에서 title_img 이미지 경로를 가져옵니다.

    Returns:
        list: 이미지 경로 리스트
    """
    conn = get_database_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT title_img FROM paper_info")
    images = cursor.fetchall()
    conn.close()
    base_path = "C:/Users/user/Documents/Github/illiterate/src/main/resources"

    for img in images:
        print(f"Image path from DB: {img[0]}")  # 각 이미지 경로 출력

    return [os.path.join(base_path, img[0]) for img in images]


def get_vectors_by_type(image_type):
    """
    img_info 테이블에서 주어진 타입의 벡터값을 가져옵니다.

    Args:
        image_type (str): 문서 타입

    Returns:
        list: 벡터 리스트
    """
    conn = get_database_connection()
    cursor = conn.cursor()
    cursor.execute(f"SELECT img_vector FROM img_info WHERE document_type = '{image_type}'")
    vectors = cursor.fetchall()
    conn.close()
    return [json.loads(vector[0]) for vector in vectors]


def get_document_info(index):
    """
    paper_info 테이블에서 주어진 id에 해당하는 document_index와 img_info를 가져옵니다.

    Args:
        index (int): 문서 id

    Returns:
        tuple: document_index와 img_info
    """
    conn = get_database_connection()
    cursor = conn.cursor()
    cursor.execute(f"SELECT document_index, img_info FROM paper_info WHERE id = {index}")
    result = cursor.fetchone()
    conn.close()
    return result