import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { AiOutlineUser, AiOutlineLock, AiOutlineMail } from "react-icons/ai";
import { CgRename } from "react-icons/cg";
import Logo from '../../../Components/Logo/Logo';
import axios from 'axios';

const Signup: React.FC = () => {
    const [userid, setUserid] = useState("");
    const [password, setPassword] = useState("");
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [domain, setDomain] = useState("직접입력");
    const [passwordError, setPasswordError] = useState("");
    const [passwordOK, setPasswordOK] = useState(false);
    const [isIdCheck, setIsIdCheck] = useState(false);
    const [isIdValid, setIsIdValid] = useState(false);
    const [idError, setIdError] = useState("");
    const [emailError, setEmailError] = useState("");

    const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        setPassword(value);
        setPasswordError(""); // 입력 중일 때는 에러 메시지를 지웁니다.
        setPasswordOK(false); // 입력 중일 때는 passwordOK를 false로 설정합니다.
    };

    const handlePasswordBlur = () => {
        // 정규식: 최소 8자, 하나 이상의 소문자, 숫자, 특수문자
        const regex = /^(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[a-z\d@$!%*?&]{8,}$/;

        if (!regex.test(password)) {
            setPasswordError("비밀번호는 최소 8자, 하나 이상의 소문자, 숫자, 특수문자가 포함되어야 합니다.");
            setPasswordOK(false);
        } else {
            setPasswordError("");
            setPasswordOK(true);
        }

        if (password === "") {
            setPasswordError("");
            setPasswordOK(false);
        }
    };

    const handleFocusPassword = () => {
        setPasswordError("");
        setPasswordOK(false);
    };

    const handleIdBlur = () => {
        if (userid === "") {
            setIsIdCheck(false);
        }
    };

    const handleCheckDuplicate = () => {
        // 아이디 중복 확인 로직을 여기에 추가합니다.
        // 예를 들어, 서버와 통신하여 아이디 중복 확인 결과를 받아오도록 할 수 있습니다.
        const idRegex = /^[a-z]+$/; // 영문 소문자만 가능

        if (!idRegex.test(userid)) {
            setIdError("아이디는 영문 소문자만 사용 가능합니다.");
            setIsIdValid(false);
            setIsIdCheck(true);
            return;
        }

        setIdError(""); // 정규식 통과

        const isAvailable = userid !== "takenId"; // 예시: "takenId"는 이미 사용 중인 아이디

        if (isAvailable) {
            setIsIdValid(true);
        } else {
            setIsIdValid(false);
        }
        setIsIdCheck(true);
    };

    const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        setEmail(value);
        setEmailError(""); // 입력 중일 때는 에러 메시지를 지웁니다.
    };

    const handleEmailBlur = () => {
        // 정규식: 이메일 형식 확인
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailRegex.test(email)) {
            setEmailError("유효한 이메일 주소를 입력해주세요.");
        } else {
            setEmailError("");
        }

        if (email === "") {
            setEmailError("");
        }
    };

    const handleSignup = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const signupData = {
                userid,
                username,
                password,
                email
            };
            console.log("회원가입 요청 데이터:", signupData);
            const response = await axios.post('/join', signupData);
            console.log(response.data);
            // 회원가입 성공 후 처리 로직을 여기에 추가합니다.
        } catch (error) {
            console.error(error);
            // 에러 처리 로직을 여기에 추가합니다.
        }
    };

    return (
        <div className="flex min-h-screen items-center justify-center px-4 bg-gray-100">
            <div className="w-full max-w-lg mx-auto bg-white rounded-lg shadow-md overflow-hidden p-8">
                <div className="flex items-center justify-center my-4">
                    <Link to="/"><Logo /></Link>
                </div>
                <div className="flex items-center justify-between my-2">
                    <Link to="/auth/login" className="text-sm text-blue-500 hover:underline">
                        로그인하러가기
                    </Link>
                </div>
                <div className="px-8 py-6">
                    <div className="text-center font-bold text-blue-500 text-xl mb-4">회원가입</div>
                    <form onSubmit={handleSignup}>
                        <div className="flex items-center mb-2 border rounded-md focus-within:border-black focus-within:text-black">
                            <AiOutlineUser className="text-gray-400 h-6 w-6 ml-2 focus-within:text-black" />
                            <input
                                type="text"
                                placeholder="아이디"
                                value={userid}
                                onChange={(e) => setUserid(e.target.value)}
                                onBlur={handleIdBlur}
                                className="block w-full px-4 py-2 ml-2 focus:outline-none rounded-r-md"
                            />
                            <button
                                type="button"
                                onClick={handleCheckDuplicate}
                                className="text-sm text-white bg-blue-500 px-4 py-3 rounded-md ml-2 whitespace-nowrap"
                            >
                                중복확인
                            </button>
                        </div>
                        {isIdCheck && (
                            <div className={`text-xs mb-1 ${isIdValid ? 'text-green-500' : 'text-red-500'}`}>
                                {isIdValid ? '사용 가능한 아이디입니다.' : idError}
                            </div>
                        )}
                        <div className="flex items-center mb-2 border rounded-md focus-within:border-black focus-within:text-black">
                            <AiOutlineLock className="text-gray-400 h-6 w-6 ml-2 focus-within:text-black" />
                            <input
                                type="password"
                                placeholder="비밀번호"
                                value={password}
                                onChange={handlePasswordChange}
                                onBlur={handlePasswordBlur}
                                onFocus={handleFocusPassword}
                                className="block w-full px-4 py-2 ml-2 focus:outline-none rounded-r-md"
                            />
                        </div>
                        {passwordError && (
                            <div className="text-red-500 text-xs mb-1">
                                {passwordError}
                            </div>
                        )}
                        {passwordOK && (
                            <div className="text-green-500 text-xs mb-1">
                                사용 가능한 비밀번호입니다.
                            </div>
                        )}
                        <div className="flex items-center mb-2 border rounded-md focus-within:border-black focus-within:text-black">
                            <CgRename className="text-gray-400 h-6 w-6 ml-2 focus-within:text-black" />
                            <input
                                type="text"
                                placeholder="이름"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                className="block w-full px-4 py-2 ml-2 focus:outline-none rounded-r-md"
                            />
                        </div>
                        <div className="flex items-center mb-2 border rounded-md focus-within:border-black focus-within:text-black">
                            <AiOutlineMail className="text-gray-400 h-6 w-6 ml-2 focus-within:text-black" />
                            <input
                                type="email"
                                placeholder="이메일"
                                value={email}
                                onChange={handleEmailChange}
                                onBlur={handleEmailBlur}
                                className="block w-full px-4 py-2 ml-2 focus:outline-none rounded-r-md"
                            />
                        </div>
                        {emailError && (
                            <div className="text-red-500 text-xs mb-1">
                                {emailError}
                            </div>
                        )}
                        <button
                            type="submit"
                            className="w-full px-4 py-2 text-white bg-blue-500 rounded-md hover:bg-blue-600 focus:outline-none"
                        >
                            회원가입
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};
export default Signup;
