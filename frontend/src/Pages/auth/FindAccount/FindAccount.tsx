import React, { useState, useEffect } from "react";
import { Link, useLocation } from "react-router-dom";
import Logo from "../../../Components/Logo/Logo";

const FindAccount: React.FC = () => {
    const [email, setEmail] = useState<string>("");
    const [userid, setUserid] = useState<string>("");
    const [message, setMessage] = useState<string>("");
    const [isFindingUsername, setIsFindingUsername] = useState<boolean>(true);
    const location = useLocation();

    useEffect(() => {
        if (location.state?.tab === "password") {
            setIsFindingUsername(false);
        }
    }, [location.state]);

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const endpoint = isFindingUsername ? "/findid" : "/send-reset-password-link";
        const data = isFindingUsername ? { email } : { userid, email };

        try {
            const response = await fetch(endpoint, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                const result = await response.json();
                setMessage(isFindingUsername ? `아이디: ${result.userid}` : "비밀번호 재설정 링크가 이메일로 전송되었습니다.");
            } else {
                setMessage(isFindingUsername ? "아이디를 찾을 수 없습니다." : "비밀번호를 찾을 수 없습니다.");
            }
        } catch (error) {
            setMessage("오류가 발생했습니다. 다시 시도해주세요.");
        }
    };

    const handleTabClick = (isUsernameTab: boolean) => {
        setIsFindingUsername(isUsernameTab);
        setMessage(""); // 탭 전환 시 메시지 초기화
    };

    return (
        <div className="flex min-h-screen items-center justify-center px-4">
            <div className="w-full max-w-xs sm:max-w-sm md:max-w-md lg:max-w-lg mx-auto">
                <div className="flex items-center justify-center my-4">
                    <Link to="/"><Logo /></Link>
                </div>
                <div className="border rounded-md overflow-hidden w-full max-w-xs sm:max-w-sm md:max-w-md lg:max-w-lg mx-auto">
                    <div className="px-4 py-6">
                        <div className="flex justify-center mb-4">
                            <button
                                onClick={() => handleTabClick(true)}
                                className={`w-1/2 px-4 py-2 ${isFindingUsername ? 'text-blue-700 border-b-2 border-blue-700' : 'text-gray-500'}`}
                            >
                                아이디 찾기
                            </button>
                            <button
                                onClick={() => handleTabClick(false)}
                                className={`w-1/2 px-4 py-2 ${!isFindingUsername ? 'text-blue-700 border-b-2 border-blue-700' : 'text-gray-500'}`}
                            >
                                비밀번호 찾기
                            </button>
                        </div>
                        <div className="text-center font-bold text-blue-300 text-xl mb-4">
                            {isFindingUsername ? "아이디 찾기" : "비밀번호 찾기"}
                        </div>
                        {message && <div className="text-center text-red-500 mb-4">{message}</div>}
                        <form onSubmit={handleSubmit}>
                            {!isFindingUsername && (
                                <input
                                    type="text"
                                    placeholder="아이디"
                                    value={userid}
                                    onChange={(e) => setUserid(e.target.value)}
                                    className="block w-full px-4 py-2 mb-4 border rounded-md focus:outline-none"
                                />
                            )}
                            <input
                                type="email"
                                placeholder="이메일"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className="block w-full px-4 py-2 mb-4 border rounded-md focus:outline-none"
                            />
                            <button
                                type="submit"
                                className="w-full px-4 py-2 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none"
                            >
                                {isFindingUsername ? "아이디 찾기" : "비밀번호 찾기"}
                            </button>
                        </form>
                    </div>
                </div>
                <ul className="flex pb-4 justify-center gap-5">
                    <li className="relative inline-block px-2 md:px-4">
                        <Link to="/auth/login" className="text-sm text-gray-400">로그인</Link>
                    </li>
                    <li className="relative inline-block px-2 md:px-4">
                        <Link to="/auth/signup" className="text-sm text-gray-400">회원가입</Link>
                    </li>        
                </ul>
            </div>
        </div>
    );
};

export default FindAccount;