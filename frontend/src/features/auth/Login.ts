import axios from 'axios';
import { Dispatch, SetStateAction } from 'react';

export const handleBasicSubmit = async (
    userid: string,
    password: string,
    setMessage: Dispatch<SetStateAction<string>>
) => {
    console.log("submiterror");
    try {
        const response = await axios.post("/api/login", null, {
            params: {
                username: userid,
                password: password
            },
        });
        console.log("submiterror");
        const authHeader = response.headers['authorization'];
        if (authHeader) {
            localStorage.setItem('authToken', authHeader);
        }

        console.log("front : " + userid + ", " + password);
        setMessage(`로그인 성공: ${authHeader}`);
    } catch (error) {
        if (axios.isAxiosError(error) && error.response) {
            console.log(userid+", "+password);

            setMessage("유효하지 않은 자격 증명");
        } else {
            console.log(userid+", "+password);
            setMessage("로그인 중 오류 발생");
        }
    }
};

export const handleFetchProtectedResource = async () => {
    try {
        const token = localStorage.getItem("authToken");
        const response = await axios.get("/api/protected", {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        console.log("Protected resource:", response.data);
    } catch (error) {
        console.error("Failed to fetch protected resource:", error);
    }
};
