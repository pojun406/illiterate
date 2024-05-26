const AccessToken = (): string => {
    try {
        const token = localStorage.getItem('authToken');
        if (!token) {
            return '토큰이 없습니다.';
        }
        return '토큰이 있습니다.';
    } catch (error:any) {
        return `에러가 발생했습니다: ${error.message}`;
    }
};

export default AccessToken;