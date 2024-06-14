const fetchWithAuth = async (apiUrl: string, requestParameters: JSON | FormData): Promise<Response | string> => {
    const getAccessToken = async (): Promise<string | null> => {
        let token = localStorage.getItem('authToken');
        if (!token) {
            return null;
        }
        return token;
    };

    const token = await getAccessToken();
    if (!token) {
        return "인증 토큰이 없습니다.";
    }

    const isFormData = requestParameters instanceof FormData;
    let response = await fetch(apiUrl, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            ...(isFormData ? {} : { 'Content-Type': 'application/json' })
        },
        body: isFormData ? requestParameters : JSON.stringify(requestParameters)
    });

    if (response.status === 401) {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) {
            console.log("리프레시 토큰이 없습니다.");
            return "리프레시 토큰이 없습니다.";
        }

        const refreshResponse = await fetch('/refresh', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${refreshToken}`
            }
        });

        if (refreshResponse.ok) {
            const data = await refreshResponse.json();
            const token = data.accessToken;
            if (token) {
                localStorage.setItem('authToken', token);
                response = await fetch(apiUrl, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        ...(isFormData ? {} : { 'Content-Type': 'application/json' })
                    },
                    body: isFormData ? requestParameters : JSON.stringify(requestParameters)
                });
                return response;
            } else {
                return "새로운 토큰을 가져오지 못했습니다.";
            }
        } else {
            console.error("리프레시 토큰 요청 실패:", refreshResponse.statusText);
            return `리프레시 토큰 요청 실패: ${refreshResponse.statusText}`;
        }
    }

    return response;
};

export default fetchWithAuth;
