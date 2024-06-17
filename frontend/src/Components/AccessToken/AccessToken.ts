import axios, { AxiosResponse } from "axios";

const fetchWithAuth = async (apiUrl: string, requestParameters: JSON | FormData): Promise<AxiosResponse | string> => {
    const getToken = async (key: string): Promise<string | null> => {
        const data = localStorage.getItem(key);
        return data ? data : null;
    };

    const accessToken = await getToken('authToken');
    const refreshToken = await getToken('refreshToken');

    if (!accessToken || !refreshToken) {
        return "인증 토큰이 없습니다.";
    }

    const isFormData = requestParameters instanceof FormData;

    const requestOptions = {
        headers: {
            'Content-Type': isFormData ? 'multipart/form-data' : 'application/json',
            'Authorization': `Bearer ${accessToken}`
        }
    };

    try {
        const initialResponse = await axios.post(apiUrl, isFormData ? requestParameters : JSON.stringify(requestParameters), requestOptions);
        return initialResponse;
    } catch (error) {
        if (axios.isAxiosError(error) && error.response && error.response.status === 401) {
            // 토큰이 만료된 경우, 갱신 시도
            try {
                const refreshRequestOptions = {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${refreshToken}`
                    }
                    // body : 'refreshToken' : '${refreshToken}'
                };

                const refreshResponse = await axios.post('/refresh', { refreshToken }, refreshRequestOptions);

                const { accessToken: newAccessToken, refreshToken: newRefreshToken } = refreshResponse.data.data;
                if (newAccessToken && newRefreshToken) {
                    localStorage.setItem('authToken', newAccessToken);
                    localStorage.setItem('refreshToken', newRefreshToken);

                    // 새로운 토큰으로 원래 요청 재시도
                    requestOptions.headers['Authorization'] = `Bearer ${newAccessToken}`;
                    const retryResponse = await axios.post(apiUrl, isFormData ? requestParameters : JSON.stringify(requestParameters), requestOptions);
                    return retryResponse;
                } else {
                    return "토큰 갱신에 실패했습니다.";
                }
            } catch (refreshError) {
                return "토큰 갱신 요청 중 오류가 발생했습니다.";
            }
        } else {
            return "요청 중 오류가 발생했습니다.";
        }
    }
};

export default fetchWithAuth;
