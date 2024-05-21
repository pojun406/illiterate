import React, { useState } from "react";
import { AiOutlineUser, AiOutlineLock } from "react-icons/ai";
import axios from "axios";

const LoginForm = () => {
    const [userid, setUserid] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");

    const handleBasicSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const response = await axios.post("/api/login", null, {
                params: {
                    userid: userid,
                    password: password
                }
            });
            setMessage("로그인 성공: " + response.data);
        } catch (error) {
            if (axios.isAxiosError(error) && error.response) {
                setMessage("유효하지 않은 자격 증명");
            } else {
                setMessage("로그인 중 오류 발생");
            }
        }
    };

    return (
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
                    <a href="/find-password" className="text-sm text-gray-400">비밀번호 찾기</a>
                </li>
                <li className="relative inline-block px-2 md:px-4">
                    <a href="/find-username" className="text-sm text-gray-400">아이디 찾기</a>
                </li>
                <li className="relative inline-block px-2 md:px-4">
                    <a href="/auth/signup" className="text-sm text-gray-400">회원가입</a>
                </li>
            </ul>
        </div>
    );
};

export default LoginForm;
