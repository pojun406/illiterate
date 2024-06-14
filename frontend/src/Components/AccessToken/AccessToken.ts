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
            'Content-Type': isFormData ? 'application/x-www-form-urlencoded' : 'application/json',
            'Authorization': `Bearer ${accessToken}`
        },
        data: isFormData ? requestParameters : JSON.stringify(requestParameters)
    };

    const initialResponse = await axios.post(apiUrl, requestOptions);

    if (initialResponse.status === 401) {
        const refreshResponse = await axios.post('/refresh', {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${refreshToken}`
            },
            data: JSON.stringify({ "id": id }),
        });
        if (refreshResponse.data) {
            const { accessToken, refreshToken, id } = refreshResponse.data.data;
            localStorage.setItem('authToken', accessToken);
            localStorage.setItem('refreshToken', refreshToken);
            localStorage.setItem('id', id);

            // 재발급 받은 토큰으로 동일한 요청 재실행
            requestOptions.headers['Authorization'] = `Bearer ${accessToken}`;
            const retryResponse = await axios.post(apiUrl, requestOptions);
            return retryResponse;
        } else {
            return "토큰 갱신에 실패했습니다.";
        }
    }

    return initialResponse;
};

export default fetchWithAuth;
