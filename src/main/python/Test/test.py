import sys
import io

# 표준 출력 (stdout) 및 표준 에러 (stderr)를 UTF-8로 설정
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')

# 테스트용 메시지 출력
print("UTF-8 테스트 메시지 출력입니다.")

if len(sys.argv) < 2:
    print("Error: No argument provided.")
    sys.exit(1)

# 인자로 받은 값 출력 (UTF-8로 처리)
print(f"Received argument: {sys.argv[1]}")
