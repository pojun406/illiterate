import json
import mysql.connector


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
        dict: title_vector 정보
    """
    conn = get_database_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT title_vector FROM paper_info WHERE document_index=1")
    vector = cursor.fetchone()
    conn.close()
    return vector[0]


def get_images_from_db():
    """
    paper_info 테이블에서 title_text 이미지 경로를 가져옵니다.

    Returns:
        list: 이미지 경로 리스트
    """
    conn = get_database_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT title_text FROM paper_info")
    images = cursor.fetchall()
    conn.close()
    return [img[0] for img in images]


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
