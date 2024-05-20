import React, { useState } from "react";
import { AiOutlineLock } from "react-icons/ai";

interface PasswordInputProps {
    setPassword: (password: string) => void;
}

const PasswordInput: React.FC<PasswordInputProps> = ({ setPassword }) => {
    const [password, setPasswordState] = useState("");
    const [isValid, setIsValid] = useState(true);
    const [errorMessage, setErrorMessage] = useState("");
    const [isFocused, setIsFocused] = useState(false);

    const handleBlur = () => {
        setIsFocused(false);
        const passwordPattern = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/;
        if (!passwordPattern.test(password)) {
            setIsValid(false);
            setErrorMessage("비밀번호는 최소 8자 이상이어야 하며, 문자와 숫자를 포함해야 합니다.");
        } else {
            setIsValid(true);
            setErrorMessage("");
        }
    };

    const handleFocus = () => {
        setIsFocused(true);
    };

    const inputTextColor = () => {
        if (isFocused) return 'text-black';
        if (!isValid && password) return 'text-red-500';
        return 'text-gray-300';
    };

    const inputBorderColor = () => {
        if (isFocused) return 'border-black';
        if (!isValid && password) return 'border-red-500';
        return 'border-gray-300';
    };

    const iconColor = () => {
        if (isFocused) return 'text-black';
        if (!isValid && password) return 'text-red-500';
        return 'text-gray-400';
    };

    return (
        <>
            <div className={`flex items-center mb-4 border rounded-md ${inputBorderColor()}`}>
                <AiOutlineLock className={`h-6 w-6 ml-2 ${iconColor()}`} />
                <input
                    type="password"
                    placeholder="비밀번호"
                    value={password}
                    onChange={(e) => {
                        setPasswordState(e.target.value);
                        setPassword(e.target.value);
                    }}
                    onBlur={handleBlur}
                    onFocus={handleFocus}
                    className={`block w-full px-4 py-2 ml-2 focus:outline-none rounded-l-md rounded-r-md ${inputTextColor()}`}
                />
            </div>
            {!isValid && password && <p className="text-red-500 text-sm">{errorMessage}</p>}
        </>
    );
};

export default PasswordInput;
