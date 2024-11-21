import React, { useState } from "react";
import { Link } from "react-router-dom";
import Logo from "../../../Components/Logo/Logo";
import axios from "axios";

const ResetPassword: React.FC = () => {
    const [newPassword, setNewPassword] = useState<string>("");
    const [confirmPassword, setConfirmPassword] = useState<string>("");
    const [message, setMessage] = useState<string>("");
    const [passwordError, setPasswordError] = useState<string>("");
    const [confirmPasswordError, setConfirmPasswordError] = useState<string>("");

    const passwordRegex = /^(?=.*[a-z])(?=.*\d)[a-z\d!@#$%^&*()_+~`|}{[\]:;?><,./-=]{8,}$/;

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (!newPassword || !confirmPassword) {
            setMessage("모든 필드를 입력해주세요.");
            return;
        }

        if (newPassword !== confirmPassword) {
            setMessage("비밀번호가 일치하지 않습니다.");
            return;
        }
        const urlParams = new URLSearchParams(window.location.search);
        const email = urlParams.get('email');
        try {
            const response = await axios.post('/public/findPassword', { email, newPassword });
            console.log(response);
            if (response.status === 200) {
                setMessage("비밀번호가 성공적으로 재설정되었습니다.");
            } else {
                setMessage("비밀번호 재설정에 실패했습니다. 다시 시도해주세요.");
            }
        } catch (error) {
            console.error(error);
            setMessage("오류가 발생했습니다. 다시 시도해주세요.");
        }
        
    };

    const handlePasswordBlur = () => {
        if (!newPassword || passwordRegex.test(newPassword)) {
            setPasswordError("");
        } else {
            setPasswordError("비밀번호는 영문 소문자와 숫자를 조합한 8자리 이상이어야 합니다.");
        }
    };

    const handleConfirmPasswordBlur = () => {
        if (passwordError) {
            setConfirmPasswordError("");
        } else if (newPassword !== confirmPassword) {
            setConfirmPasswordError("비밀번호가 일치하지 않습니다.");
        } else {
            setConfirmPasswordError("");
        }
    };

    return (
        <div className="flex min-h-screen items-center justify-center px-4 bg-gray-100">
            <div className="w-full max-w-md mx-auto bg-white rounded-lg shadow-md overflow-hidden">
                <div className="flex items-center justify-center my-4">
                    <Link to="/"><Logo /></Link>
                </div>
                <div className="px-4 py-6">
                    <div className="text-center font-bold text-blue-300 text-xl mb-4">
                        비밀번호 재설정
                    </div>
                    {message && (
                        <div className="text-center text-red-500 mb-4">
                            {message}
                        </div>
                    )}
                    <form onSubmit={handleSubmit}>
                        <div className="flex items-center mb-4 space-x-2">
                            <input
                                type="password"
                                placeholder="새 비밀번호"
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                                onBlur={handlePasswordBlur}
                                className="block w-full px-4 py-2 border rounded-md focus:outline-none"
                            />
                        </div>
                        {passwordError && (
                            <div className="text-red-500 text-sm mb-4">
                                {passwordError}
                            </div>
                        )}
                        <div className="flex items-center mb-4 space-x-2">
                            <input
                                type="password"
                                placeholder="비밀번호 확인"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                onBlur={handleConfirmPasswordBlur}
                                className="block w-full px-4 py-2 border rounded-md focus:outline-none"
                            />
                        </div>
                        {confirmPasswordError && (
                            <div className="text-red-500 text-sm mb-4">
                                {confirmPasswordError}
                            </div>
                        )}
                        <button
                            type="submit"
                            className="w-full px-4 py-2 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none"
                        >
                            비밀번호 재설정
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

export default ResetPassword;
