import axios from "axios";

const AccessToken = () => {
    try {
        const token = localStorage.getItem('authToken');
        if (!token) {
            return '토큰이 없습니다.';
        } else {
            // 토큰이 있는 경우 Authorization 헤더 설정
            axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
            return '토큰이 있습니다.';
        }
    } catch (error: any) {
        return `에러가 발생했습니다: ${error.message}`;
    }
};

export default AccessToken;
