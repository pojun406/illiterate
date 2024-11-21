import axios, { AxiosResponse } from "axios";

const fetchWithAuthGet = async (apiUrl: string, requestParameters?: any): Promise<AxiosResponse | string> => {
    const getToken = async (key: string): Promise<string | null> => {
        const data = localStorage.getItem(key);
        return data ? data : null;
    };

    const accessToken = await getToken('authToken');

    if (!accessToken) {
        alert('로그인을 한 이후 진행해주세요.');
        window.location.href = '/auth/login';
        return "토큰이 없습니다.";
    }

    try {
        const params = { ...requestParameters };
        const response = await axios.get(apiUrl, {
            headers: {
                'Authorization': `Bearer ${accessToken}`
            },
            params
        });

        console.log(apiUrl + ' 요청 성공:', response);
        return response;
    } catch (error: any) {
        if (error.response && error.response.status === 401) {
            localStorage.clear();
            alert('로그인이 만료되었습니다. 다시 로그인해주세요.');
            window.location.href = '/auth/login';
            return "인증이 만료되었습니다.";
        } else if (error.response && error.response.status === 400) {
            console.error('잘못된 요청:', error.response.data);
            return error.response;
        } else {
            console.error('요청 중 오류:', error);
            if (error.response) {
                console.error('서버 응답 데이터:', error.response.data);
                console.error('서버 응답 상태:', error.response.status);
                console.error('서버 응답 헤더:', error.response.headers);
            }
            return "요청 중 오류가 발생했습니다.";
        }
    }
};

export default fetchWithAuthGet;
