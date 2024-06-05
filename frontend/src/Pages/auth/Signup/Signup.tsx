import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Logo from "../../../Components/Logo/Logo";
import { AiOutlineLock, AiOutlineUser } from "react-icons/ai";
import { CgRename } from "react-icons/cg";
import { MdOutlineMail } from "react-icons/md";
import { FaRegEye, FaRegEyeSlash } from "react-icons/fa";

const Signup: React.FC = () => {
    const [userid, setUserid] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [showPassword, setShowPassword] = useState<boolean>(false);
    const [username, setUsername] = useState<string>("");
    const [emailUser, setEmailUser] = useState<string>("");
    const [emailDomain, setEmailDomain] = useState<string>("");
    const [passwordError, setPasswordError] = useState<string>("");
    const [passwordOK, setPasswordOK] = useState<boolean>(false);
    const [isIdCheck, setIsIdCheck] = useState(false);
    const [isIdValid, setIsIdValid] = useState<boolean | null>(null); // null: 초기 상태, true: 사용 가능, false: 사용 불가
    const [idError, setIdError] = useState<string>("");

    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
    
        const email = `${emailUser}@${emailDomain}`;
        const data = { userid, password, username, email };
    
        try {
            const response = await fetch("/join", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            });
    
            if (response.ok) {
                const responseText = await response.text();
                if (responseText === "ok") {
                    console.log("회원가입 성공");
    
                    // 회원가입 성공 후 로그인 시도
                    const loginResponse = await fetch("/login", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify({ userid, password })
                    });
    
                    if (loginResponse.ok) {
                        const loginData = await loginResponse.json();
                        localStorage.setItem('authToken', loginData.accessToken);
                        localStorage.setItem('refreshToken', loginData.refreshToken);
                        navigate("/");
                    } else {
                        const loginError = await loginResponse.json().catch(() => ({ message: loginResponse.statusText }));
                        console.error("Login Error:", loginError.message);
                        alert("로그인에 실패했습니다. 다시 시도해주세요.");
                    }
                } else {
                    console.error("Unexpected response:", responseText);
                    alert("회원가입에 성공했지만, 응답 처리 중 문제가 발생했습니다.");
                }
            } else {
                const errorText = await response.text();
                console.error("Error:", errorText);
                try {
                    const errorJson = JSON.parse(errorText);
                    if (errorJson.errorMessage) {
                        alert(errorJson.errorMessage);
                    } else {
                        alert("회원가입에 실패했습니다. 다시 시도해주세요.");
                    }
                } catch (parseError) {
                    alert("회원가입에 실패했습니다. 다시 시도해주세요.");
                }
            }
        } catch (error) {
            console.error("Error:", error);
            alert("서버와의 통신 중 오류가 발생했습니다. 다시 시도해주세요.");
        }
    };
    
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

    const handleIdChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setUserid(e.target.value);
        setIsIdCheck(false);
        setIsIdValid(null);
        setIdError("");
    };

    const toggleShowPassword = () => {
        setShowPassword(!showPassword);
    };

    return (
        <div className="flex min-h-screen items-center justify-center px-4">
            <div className="w-full max-w-xs sm:max-w-sm md:max-w-md lg:max-w-lg mx-auto">
                <div className="flex items-center justify-center my-4">
                    <Link to="/">
                        <Logo />
                    </Link>
                </div>
                <div className="flex justify-start mb-4">
                    <Link to="/auth/login" className="text-sm text-blue-700">← 로그인하러가기</Link>
                </div>
                <div className="border rounded-md overflow-hidden w-full max-w-xs sm:max-hidden w-full max-w-xs sm:max-w-sm md:max-w-md lg:max-w-lg mx-auto">
                    <div className="px-4 py-6">
                        <div className="text-center font-bold text-blue-300 text-xl mb-4">회원가입</div>
                        <form onSubmit={handleSubmit}>
                            <div
                                className={`flex items-center mb-4 border rounded-md ${isIdCheck && isIdValid === false ? 'border-red-500' : isIdCheck && isIdValid === true ? 'border-green-500' : 'border-gray-400'} focus-within:border-black focus-within:text-black`}>
                                <AiOutlineUser className={`text-gray-400 h-6 w-6 ml-2 ${isIdCheck && isIdValid === false ? 'text-red-500' : isIdCheck && isIdValid === true ? 'text-green-500' : 'text-gray-400'}`} />
                                <input
                                    type="text"
                                    placeholder="아이디"
                                    value={userid}
                                    onChange={handleIdChange}
                                    onBlur={handleIdBlur}
                                    disabled={isIdCheck && isIdValid === true}
                                    className="block w-full px-4 py-2 ml-2 focus:outline-none"
                                />
                                <button
                                    type="button"
                                    onClick={handleCheckDuplicate}
                                    className="ml-2 px-4 py-2 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none whitespace-nowrap"
                                >
                                    중복확인
                                </button>
                            </div>
                            {idError && (
                                <div className="text-red-500 text-xs mb-2">
                                    {idError}
                                </div>
                            )}
                            {isIdCheck && isIdValid === true && (
                                <div className="text-green-500 text-xs mb-2">
                                    사용 가능한 아이디입니다.
                                </div>
                            )}
                            {isIdCheck && isIdValid === false && userid !== "" && !idError && (
                                <div className="text-red-500 text-xs mb-2">
                                    사용할 수 없는 아이디입니다.
                                </div>
                            )}
                            <div
                                className={`flex items-center mb-4 border rounded-md ${passwordError ? 'border-red-500 text-red-500' : passwordOK ? 'border-green-500 text-green-500' : 'border-gray-400'} focus-within:border-black focus-within:text-black`}>
                                <AiOutlineLock
                                    className={`h-6 w-6 ml-2 ${passwordError ? 'text-red-500' : passwordOK ? 'text-green-500' : 'text-gray-400'} focus-within:text-black`} />
                                <input
                                    type={showPassword ? "text" : "password"}
                                    placeholder="비밀번호(소문자, 숫자, 특수문자 포함 최소 8자리 이상)"
                                    value={password}
                                    onChange={handlePasswordChange}
                                    onBlur={handlePasswordBlur}
                                    onFocus={handleFocusPassword}
                                    className="block w-full px-4 py-2 ml-2 focus:outline-none"
                                />
                                <button
                                    type="button"
                                    onClick={toggleShowPassword}
                                    className="ml-2 px-4 py-2 focus:outline-none"
                                >
                                    {showPassword ? <FaRegEyeSlash /> : <FaRegEye />}
                                </button>
                            </div>
                            {passwordError && (
                                <div className="text-red-500 text-xs mb-2">
                                    {passwordError}
                                </div>
                            )}
                            {passwordOK && (
                                <div className="text-green-500 text-xs mb-2">
                                    사용 가능한 비밀번호입니다.
                                </div>
                            )}
                            <div
                                className="flex items-center mb-4 border rounded-md focus-within:border-black focus-within:text-black">
                                <CgRename className="text-gray-400 h-6 w-6 ml-2 focus-within:text-black" />
                                <input
                                    type="text"
                                    placeholder="이름"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    className="block w-full px-4 py-2 ml-2 focus:outline-none"
                                />
                            </div>
                            <div
                                className="flex items-center mb-4 border rounded-md focus-within:border-black focus-within:text-black">
                                <MdOutlineMail className="text-gray-400 h-6 w-6 ml-2 focus-within:text-black" />
                                <input
                                    type="text"
                                    placeholder="이메일"
                                    value={emailUser}
                                    onChange={(e) => setEmailUser(e.target.value)}
                                    className="block w-1/3 px-4 py-2 ml-2 focus:outline-none"
                                />
                                <span className="px-2">@</span>
                                <input
                                    type="text"
                                    placeholder="도메인"
                                    value={emailDomain}
                                    onChange={(e) => setEmailDomain(e.target.value)}
                                    className="block w-1/3 px-4 py-2 focus:outline-none"
                                />
                                <select
                                    value={emailDomain}
                                    onChange={(e) => setEmailDomain(e.target.value)}
                                    className="block w-1/3 px-4 py-2 ml-2 focus:outline-none"
                                >
                                    <option value="">직접입력</option>
                                    <option value="naver.com">naver.com</option>
                                    <option value="gmail.com">gmail.com</option>
                                    <option value="daum.net">daum.net</option>
                                </select>
                            </div>
                            <button
                                type="submit"
                                className="w-full px-4 py-2 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none"
                            >
                                회원가입
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Signup;