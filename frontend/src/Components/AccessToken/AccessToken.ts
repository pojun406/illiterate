import axios, { AxiosResponse } from "axios";

const fetchWithAuth = async (apiUrl: string, requestParameters: JSON | FormData): Promise<AxiosResponse | string> => {
    const getToken = async (key: string): Promise<string | null> => {
        const data = localStorage.getItem(key);
        return data ? data : null;
    };

    const accessToken = await getToken('authToken');
    if (!accessToken) {
        return "인증 토큰이 없습니다.";
    }

    const refreshToken = await getToken('refreshToken');
    if (!refreshToken) {
        return "인증 토큰이 없습니다.";
    }

    const id = await getToken('id');
    if (!id) {
        return "id값이 없습니다.";
    }

    const isFormData = requestParameters instanceof FormData;

    const requestOptions = {
        headers: {
            'Content-Type': isFormData ? 'multipart/form-data' : 'application/json',
            'Authorization': `Bearer ${accessToken}`
        }
    };
        const initialResponse = await axios.post(apiUrl, isFormData ? requestParameters : JSON.stringify(requestParameters), requestOptions);

        if (initialResponse.status === 401) {
            // 토큰이 만료된 경우, 갱신 시도
            try {
                const refreshResponse = await axios.post('/refresh', {
                    id: id
                }, {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${refreshToken}`
                    }
                });

                const { accessToken: newAccessToken, refreshToken: newRefreshToken, id: newId } = refreshResponse.data.data;
                if (newAccessToken && newRefreshToken && newId) {
                    localStorage.setItem('authToken', newAccessToken);
                    localStorage.setItem('refreshToken', newRefreshToken);
                    localStorage.setItem('id', newId);

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
        }

        return initialResponse;
};

export default fetchWithAuth;
