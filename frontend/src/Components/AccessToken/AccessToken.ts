import axios, { AxiosResponse } from "axios";

const fetchWithAuth = async (apiUrl: string, requestParameters?: any): Promise<AxiosResponse | string> => {
    const getToken = async (key: string): Promise<string | null> => {
        const data = localStorage.getItem(key);
        return data ? data : null;
    };

    const accessToken = await getToken('authToken');
    const refreshToken = await getToken('refreshToken');

    if (!accessToken || !refreshToken) {
        return "토큰이 없습니다.";
    }

    try {
        const response = await axios.post(apiUrl, requestParameters, {
            headers: {
                'Authorization': `Bearer ${accessToken}`,
                'Content-Type': 'application/json'
            }
        });
        return response;
    } catch (error: any) {
        if (error.response && error.response.status === 401) {
            try {
                const refreshResponse = await axios.post('/api/refresh', { token: refreshToken });
                const newAccessToken = refreshResponse.data.accessToken;
                localStorage.setItem('authToken', newAccessToken);

                const retryResponse = await axios.post(apiUrl, requestParameters, {
                    headers: {
                        'Authorization': `Bearer ${newAccessToken}`,
                        'Content-Type': 'application/json'
                    }
                });
                return retryResponse;
            } catch (refreshError) {
                return "토큰 갱신 요청 중 오류가 발생했습니다.";
            }
        } else {
            return "요청 중 오류가 발생했습니다.";
        }
    }
};

export default fetchWithAuth;
