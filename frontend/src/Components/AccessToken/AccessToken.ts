import axios, { AxiosResponse } from "axios";

const fetchWithAuth = async (apiUrl: string, requestParameters?: any, file?: File): Promise<AxiosResponse | string> => {
    const getToken = async (key: string): Promise<string | null> => {
        const data = localStorage.getItem(key);
        return data ? data : null;
    };

    const accessToken = await getToken('authToken');
    const refreshToken = await getToken('refreshToken');
    const userId = localStorage.getItem('id');

    if (!accessToken || !refreshToken) {
        return "토큰이 없습니다.";
    }

    try {
        let response;
        if (file) {
            const formData = new FormData();
            if (requestParameters) {
                formData.append('request', new Blob([JSON.stringify({ ...requestParameters, userId })], { type: 'application/json' }));
            } else {
                formData.append('request', new Blob([JSON.stringify({ userId })], { type: 'application/json' }));
            }
            formData.append('image', file);

            response = await axios.post(apiUrl, formData, {
                headers: {
                    'Authorization': `Bearer ${accessToken}`,
                    'Content-Type': 'multipart/form-data'
                }
            });
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
            try {
                const refreshResponse = await axios.post('/refresh', { refreshToken, userId }, {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
                const newAccessToken = refreshResponse.data.data.accessToken;
                localStorage.setItem('authToken', newAccessToken);

                let retryResponse;
                if (file) {
                    const formData = new FormData();
                    if (requestParameters) {
                        formData.append('request', new Blob([JSON.stringify({ ...requestParameters, userId })], { type: 'application/json' }));
                    } else {
                        formData.append('request', new Blob([JSON.stringify({ userId })], { type: 'application/json' }));
                    }
                    formData.append('image', file);

                    retryResponse = await axios.post(apiUrl, formData, {
                        headers: {
                            'Authorization': `Bearer ${newAccessToken}`,
                            'Content-Type': 'multipart/form-data'
                        }
                    });
                } else if (requestParameters) {
                    retryResponse = await axios.post(apiUrl, { ...requestParameters, userId }, {
                        headers: {
                            'Authorization': `Bearer ${newAccessToken}`,
                            'Content-Type': 'application/json'
                        }
                    });
                } else {
                    retryResponse = await axios.post(apiUrl, { userId }, {
                        headers: {
                            'Authorization': `Bearer ${newAccessToken}`,
                            'Content-Type': 'application/json'
                        }
                    });
                }
                console.log('재요청 성공:', retryResponse);
                return retryResponse;
            } catch (refreshError) {
                console.error('토큰 갱신 요청 중 오류:', refreshError);
                return "토큰 갱신 요청 중 오류가 발생했습니다.";
            }
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
