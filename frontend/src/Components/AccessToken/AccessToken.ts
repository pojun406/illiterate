const fetchWithAuth = async (apiUrl: string, requestOptions: RequestInit): Promise<Response | string> => {
    const getAccessToken = async (): Promise<string | null> => {
        let token = localStorage.getItem('authToken');
        if (!token) {
            return null;
        }

        const response = await fetch(apiUrl,
            {
                method:'POST',
                headers:{
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body:JSON.stringify({requestOptions:requestOptions})
            }
        );
        if (response.status === 401) {
            const refreshToken = localStorage.getItem('refreshToken');
            if (!refreshToken) {
                console.log("리프레시 토큰이 없습니다.")
                return null;
            }

            const refreshResponse = await fetch('/refresh', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${refreshToken}`,
                }
            });

            if (refreshResponse.ok) {
                const data = await refreshResponse.json();
                console.log("새로운 토큰:", data);
                token = data.accessToken; // Adjust the path based on your actual response structure
                if (token) {
                    localStorage.setItem('authToken', token);
                    return token;
                } else {
                    return null;
                }
            } else {
                console.error("리프레시 토큰 요청 실패:", refreshResponse.statusText);
                return null;
            }
        }

        return token;
    };

    try {
        let token = await getAccessToken();
        if (!token) {
            throw new Error('토큰이 없습니다.');
        }

        const headers = new Headers(requestOptions.headers);
        headers.set('Authorization', `Bearer ${token}`);

        let response = await fetch(apiUrl, { ...requestOptions, headers });
        if (response.status === 401) {
            token = await getAccessToken();
            if (!token) {
                throw new Error('토큰이 없습니다.');
            }
            headers.set('Authorization', `Bearer ${token}`);
            response = await fetch(apiUrl, { ...requestOptions, headers });
        }

        return response;
    } catch (error: any) {
        return `에러가 발생했습니다: ${error.message}`;
    }
};

export default fetchWithAuth;
