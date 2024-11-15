import axios, { AxiosResponse } from "axios";

const fetchWithAuth = async (apiUrl: string, requestParameters?: any, formData?: FormData): Promise<AxiosResponse | string> => {
    const getToken = async (key: string): Promise<string | null> => {
        const data = localStorage.getItem(key);
        return data ? data : null;
    };

    const accessToken = await getToken('authToken');
    const userId = localStorage.getItem('id');

    if (!accessToken) {
        alert('로그인을 한 이후 진행해주세요.');
        window.location.href = '/auth/login';
        return "토큰이 없습니다.";
    }

    try {
        let response;
        if (formData) {
            if (requestParameters) {
                formData.append('request', new Blob([JSON.stringify({ ...requestParameters, userId })], { type: 'application/json' }));
            } else {
                formData.append('request', new Blob([JSON.stringify({ userId })], { type: 'application/json' }));
            }

            const headers: Record<string, string> = {
                'Authorization': `Bearer ${accessToken}`
            };

            response = await axios.post(apiUrl, formData, { headers });
        } else if (requestParameters) {
            response = await axios.post(apiUrl, { ...requestParameters, userId }, {
                headers: {
                    'Authorization': `Bearer ${accessToken}`,
                    'Content-Type': 'application/json'
                }
            });
        } else {
            response = await axios.post(apiUrl, { userId }, {
                headers: {
                    'Authorization': `Bearer ${accessToken}`,
                    'Content-Type': 'application/json'
                }
            });
        }
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
            return "잘못된 요청입니다.";
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

export default fetchWithAuth;
