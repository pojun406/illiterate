import React, { useState, useEffect } from 'react';
import {Link, useNavigate} from 'react-router-dom';
import Logo from "../../../Components/Logo/Logo";
import { AiOutlineLock, AiOutlineUser } from "react-icons/ai";
import axios from "axios";

const Login = () => {
    const [userid, setUserid] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        handleFetchProtectedResource();
    }, []);

    const handleBasicSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const response = await axios.post("/login", { userid, password }, {
                headers: { 'Content-Type': 'application/json' }
            });
    
            if (response.data && response.data.data) {
                const { accessToken, refreshToken } = response.data.data;
                localStorage.setItem('authToken', accessToken);
                localStorage.setItem('refreshToken', refreshToken);
                setIsLoggedIn(true);
                setMessage("로그인 성공");
                navigate("/");
            } else {
                setMessage("로그인 실패: 자격 증명을 확인해주세요.");
            }
        } catch (error) {
            const errorMessage = axios.isAxiosError(error) && error.response
                ? error.response.data.message
                : "로그인 중 오류 발생";
            setMessage(errorMessage);
        }
    };
    const handleFetchProtectedResource = async () => {
        try {
            const token = localStorage.getItem("authToken");
            if (!token) {
                return;
            }

            const response = await axios.get("/api/protected", {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            console.log("Protected resource:", response.data);
            navigate('/');
        } catch (error) {
            console.error("Unexpected error:", error);
            setMessage("예기치 않은 오류가 발생했습니다.");
        }
    };

    return (
        <div className="flex min-h-screen items-center justify-center px-4">
            <div className="w-full max-w-xs sm:max-w-sm md:max-w-md lg:max-w-lg mx-auto">
                <div className="flex items-center justify-center my-4">
                    <Link to="/"><Logo /></Link>
                </div>
                <div>
                    <div className="border rounded-md overflow-hidden w-full max-w-xs sm:max-w-sm md:max-w-md lg:max-w-lg mx-auto">
                        <div className="px-4 py-6">
                            <div className="text-center font-bold text-blue-300 text-xl mb-4">로그인</div>
                            {message && <div className="text-center text-red-500 mb-4">{message}</div>}
                            <form onSubmit={handleBasicSubmit}>
                                <div className="flex items-center mb-4 border rounded-md focus-within:border-black focus-within:text-black">
                                    <AiOutlineUser className="text-gray-400 h-6 w-6 ml-2 focus-within:text-black" />
                                    <input
                                        type="text"
                                        placeholder="아이디"
                                        value={userid}
                                        onChange={(e) => setUserid(e.target.value)}
                                        className="block w-full px-4 py-2 ml-2 focus:outline-none"
                                    />
                                </div>
                                <div className="flex items-center mb-4 border rounded-md focus-within:border-black focus-within:text-black">
                                    <AiOutlineLock className="text-gray-400 h-6 w-6 ml-2 focus-within:text-black" />
                                    <input
                                        type="password"
                                        placeholder="비밀번호"
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                        className="block w-full px-4 py-2 ml-2 focus:outline-none"
                                    />
                                </div>
                                <button
                                    type="submit"
                                    className="w-full px-4 py-2 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none"
                                >
                                    로그인
                                </button>
                            </form>
                        </div>
                    </div>
                    <ul className="flex pb-4 justify-center">
                        <li className="relative inline-block px-2 md:px-4">
                            <Link to="/auth/find-account" state={{ tab: "password" }} className="text-sm text-gray-400">비밀번호 찾기</Link>
                        </li>
                        <li className="relative inline-block px-2 md:px-4">
                            <Link to="/auth/find-account" state={{ tab: "username" }} className="text-sm text-gray-400">아이디 찾기</Link>
                        </li>
                        <li className="relative inline-block px-2 md:px-4">
                            <a href="/auth/Signup" className="text-sm text-gray-400">회원가입</a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    );
};

export default Login;
