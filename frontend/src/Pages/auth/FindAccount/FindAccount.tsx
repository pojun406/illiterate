import React, { useState, useEffect } from "react";
import { Link, useLocation } from "react-router-dom";
import Logo from "../../../Components/Logo/Logo";
import axios from "axios";

const FindAccount: React.FC = () => {
    const [email, setEmail] = useState<string>("");
    const [userid, setUserid] = useState<string>("");
    const [message, setMessage] = useState<string>("");
    const [isFindingUsername, setIsFindingUsername] = useState<boolean>(true);
    const [verificationCode, setVerificationCode] = useState<string>("");
    const [isResend, setIsResend] = useState<boolean>(false);
    const [isEmailVerified, setIsEmailVerified] = useState<boolean>(false);
    const [verificationError, setVerificationError] = useState<string>("");
    const location = useLocation();

    useEffect(() => {
        if (location.state?.tab === "password") {
            setIsFindingUsername(false);
        }
    }, [location.state]);

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const endpoint = isFindingUsername ? "/findId" : `/${userid}/password`;
        const data = isFindingUsername ? { userEmail: email } : { email, verificationCode };

        if (!email) {
            setMessage("이메일을 입력해주세요.");
            return;
        }

        if (!isFindingUsername && !verificationCode) {
            setMessage("인증번호를 입력해주세요.");
            return;
        }

        console.log("서버로 보내는 데이터:", data);

        try {
            const response = await fetch(endpoint, {
                method: isFindingUsername ? "POST" : "PATCH",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                const result = await response.json();
                setMessage(isFindingUsername ? `아이디: ${result.data}` : "비밀번호 재설정 링크가 이메일로 전송되었습니다.");
            } else {
                setMessage(isFindingUsername ? "아이디를 찾을 수 없습니다." : "비밀번호를 찾을 수 없습니다.");
            }
        } catch (error) {
            setMessage("오류가 발생했습니다. 다시 시도해주세요.");
        }
    };

    const handleSendVerificationEmail = async () => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (email === "" || !emailRegex.test(email)) {
            alert("유효한 이메일 주소를 입력해주세요.");
            return;
        }

        if (isResend) {
            const confirmResend = window.confirm("인증번호를 다시 요청하시겠습니까?");
            if (!confirmResend) {
                return;
            }
        }

        try {
            const response = await axios.post('/public/CertificationNumber', { email });
            console.log(response.data);
            alert("이메일을 보냈습니다.");
            setIsResend(true);
        } catch (error) {
            console.error(error);
            alert("인증번호 전송 중 오류가 발생했습니다.");
        }
    };

    const handleVerifyCode = async () => {
        try {
            const response = await axios.post('/public/verify', { email, verificationCode });

            if (response.data.code === 200 && response.data.data.isValid) {
                setIsEmailVerified(true);
                setVerificationError("");
            } else {
                setIsEmailVerified(false);
                setVerificationError("인증번호가 올바르지 않습니다.");
            }
        } catch (error) {
            console.error(error);
            setVerificationError("인증번호 확인 중 오류가 발생했습니다.");
        }
    };

    const handleTabClick = (isUsernameTab: boolean) => {
        setIsFindingUsername(isUsernameTab);
        setMessage("");
    };

    return (
        <div className="flex min-h-screen items-center justify-center px-4 bg-gray-100">
            <div className="w-full max-w-md mx-auto bg-white rounded-lg shadow-md overflow-hidden">
                <div className="flex items-center justify-center my-4">
                    <Link to="/"><Logo /></Link>
                </div>
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
                    {(message || verificationError) && (
                        <div className="text-center text-red-500 mb-4">
                            {message || verificationError}
                        </div>
                    )}
                    <form onSubmit={handleSubmit}>
                        {!isFindingUsername && (
                            <>
                                <input
                                    type="text"
                                    placeholder="아이디"
                                    value={userid}
                                    onChange={(e) => setUserid(e.target.value)}
                                    className="block w-full px-4 py-2 mb-4 border rounded-md focus:outline-none"
                                />
                                <div className="flex items-center mb-4 space-x-2">
                                    <input
                                        type="email"
                                        placeholder="이메일"
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        className="block w-full px-4 py-2 border rounded-md focus:outline-none"
                                    />
                                    <button
                                        type="button"
                                        onClick={handleSendVerificationEmail}
                                        className="w-1/4 px-4 py-2 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none"
                                    >
                                        인증
                                    </button>
                                </div>
                                <div className="flex items-center mb-4 space-x-2">
                                    <input
                                        type="text"
                                        placeholder="인증번호"
                                        value={verificationCode}
                                        onChange={(e) => setVerificationCode(e.target.value)}
                                        className="block w-full px-4 py-2 border rounded-md focus:outline-none"
                                    />
                                    <button
                                        type="button"
                                        className="w-1/4 px-4 py-2 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none"
                                        onClick={handleVerifyCode}
                                    >
                                        확인
                                    </button>
                                </div>
                            </>
                        )}
                        {isFindingUsername && (
                            <div className="flex items-center mb-4 space-x-2">
                                <input
                                    type="email"
                                    placeholder="이메일"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    className="block w-full px-4 py-2 border rounded-md focus:outline-none"
                                />
                            </div>
                        )}
                        <button
                            type="submit"
                            className="w-full px-4 py-2 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none"
                        >
                            {isFindingUsername ? "아이디 찾기" : "비밀번호 찾기"}
                        </button>
                    </form>
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