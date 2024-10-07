import json
import mysql.connector
import os


def get_database_connection():
    """
    MySQL 데이터베이스에 연결을 생성하고 반환하는 함수.

    Returns:
        mysql.connector.connection_cext.CMySQLConnection: MySQL 데이터베이스 연결 객체
    """
    # MySQL 데이터베이스 연결 정보 설정
    connection = mysql.connector.connect(
        host='0.0.0.0',  # 데이터베이스 호스트 주소
        database='illiterate',  # 사용할 데이터베이스 이름
        user='root',  # 데이터베이스 사용자 이름
        password='REDACTED_PASSWORD'  # 데이터베이스 접속 비밀번호
    )
    return connection  # 연결 객체 반환


def get_title_vector():
    """
    `paper_info` 테이블에서 `title_vector` 값을 가져와 반환하는 함수.

    Returns:
        dict: `document_index`를 키로, `title_vector`를 값으로 하는 딕셔너리.
    """
    # 데이터베이스 연결 생성
    conn = get_database_connection()
    cursor = conn.cursor()

    # 쿼리를 실행하여 document_index와 title_vector 가져오기
    cursor.execute("SELECT document_index, title_vector FROM paper_info")
    results = cursor.fetchall()  # 모든 결과 가져오기
    conn.close()  # 연결 닫기

    title_vectors = {}  # 타이틀 벡터를 저장할 딕셔너리 초기화
    for row in results:  # 쿼리 결과 순회
        print(f"Raw data for document_index {row[0]}: {row[1]}")
        if row[1]:  # title_vector가 존재하는 경우
            try:
                # 문자열 형태의 벡터 데이터를 파싱하여 딕셔너리에 저장
                parsed_vector = eval(row[1])  # 문자열을 실제 파이썬 객체로 변환
                title_vectors[str(row[0])] = [parsed_vector]  # 리스트로 감싸서 저장
                print(f"Parsed vector for document_index {row[0]}: {parsed_vector}")
            except Exception as e:
                # 벡터 파싱 중 오류 발생 시 예외 처리
                print(f"Error parsing vector for document_index {row[0]}: {str(e)}")

    print(f"Final title_vectors: {title_vectors}")
    return title_vectors  # 최종 벡터 딕셔너리 반환


def get_images_from_db():
    """
    `paper_info` 테이블에서 `title_img` 필드에 있는 이미지 경로를 가져와 반환하는 함수.

    Returns:
        list: 절대 경로로 변환된 이미지 경로 리스트.
    """
    # 데이터베이스 연결 생성
    conn = get_database_connection()
    cursor = conn.cursor()

    # 쿼리 실행하여 title_img 필드 값 가져오기
    cursor.execute("SELECT title_img FROM paper_info")
    images = cursor.fetchall()  # 모든 이미지 경로 가져오기
    conn.close()  # 연결 닫기

    base_path = "D:/Project/illiterate/src/main/resources"  # 이미지 경로의 기본 디렉토리

    full_paths = []  # 절대 경로 리스트 초기화
    for img in images:
        # 상대 경로를 절대 경로로 변환하고 백슬래시를 슬래시로 변환
        full_path = os.path.normpath(os.path.join(base_path, img[0].lstrip('/')))
        full_path = full_path.replace('\\', '/')  # 윈도우 경로를 슬래시로 일관성 있게 변환
        print(f"Full image path: {full_path}")  # 디버깅용 출력
        full_paths.append(full_path)  # 변환된 경로를 리스트에 추가

    return full_paths  # 변환된 이미지 경로 리스트 반환


def get_vectors_by_type(image_type):
    """
    `paper_info` 테이블에서 주어진 문서 타입의 `img_info` 값을 가져오는 함수.

    Args:
        image_type (str): 문서 타입을 나타내는 문자열.

    Returns:
        dict: `img_info` 필드에서 파싱한 딕셔너리.
    """
    # 데이터베이스 연결 생성
    conn = get_database_connection()
    cursor = conn.cursor()

    # 쿼리 실행하여 주어진 타입의 img_info 가져오기
    cursor.execute(f"SELECT img_info FROM paper_info WHERE document_index = '{image_type}'")
    result = cursor.fetchone()  # 한 개의 결과 가져오기
    conn.close()  # 연결 닫기

    # 결과가 있으면 JSON 형식으로 변환, 없으면 빈 딕셔너리 반환
    if result and result[0]:
        return json.loads(result[0])
    return {}


def get_document_info(document_index):
    """
    `paper_info` 테이블에서 주어진 `document_index`에 해당하는 정보를 가져오는 함수.

    Args:
        document_index (int): 조회할 문서의 인덱스.

    Returns:
        tuple: `document_index`와 `img_info` 값을 포함하는 튜플.
    """
    # 데이터베이스 연결 생성
    conn = get_database_connection()
    cursor = conn.cursor()

    # 쿼리 실행하여 주어진 document_index에 대한 정보를 가져오기
    cursor.execute(f"SELECT document_index, img_info FROM paper_info WHERE document_index = {document_index}")
    result = cursor.fetchone()  # 한 개의 결과 가져오기
    conn.close()  # 연결 닫기

    return result  # 조회된 결과 반환 (document_index와 img_info)
