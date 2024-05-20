import React, { useState } from "react";
import { AiOutlineUser } from "react-icons/ai";

interface UserIdInputProps {
    onUsernameChange: (username: string) => void;
    UsernameValid: (isValid: boolean) => void;
}

const UserIdInput: React.FC<UserIdInputProps> = ({ onUsernameChange, UsernameValid }) => {
    const [username, setUsername] = useState("");
    const [isUsernameValid, setIsUsernameValid] = useState(true);

    const handleCheckUsername = () => {
        // Assuming you have a function to check username validity
        // Here, for demonstration, let's assume it's always valid
        setIsUsernameValid(true);
        UsernameValid(true);
    };

    const handleUsernameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setUsername(e.target.value);
        onUsernameChange(e.target.value);
        setIsUsernameValid(true);
    };

    return (
        <div className="flex items-center mb-4 border rounded-md focus-within:border-black focus-within:text-black">
            <AiOutlineUser className="text-gray-400 h-6 w-6 ml-2 focus-within:text-black"/>
            <input
                type="text"
                placeholder="아이디"
                value={username}
                onChange={handleUsernameChange}
                className="flex-1 px-4 py-2 ml-2 focus:outline-none rounded-l-md rounded-r-md"
            />
            <button
                onClick={handleCheckUsername}
                className="px-4 py-2 ml-2 bg-gray-300 rounded-md hover:bg-gray-400 focus:outline-none"
            >
                중복확인
            </button>
            {!isUsernameValid && (
                <div className="text-red-500 ml-2">이미 사용 중인 아이디입니다.</div>
            )}
        </div>
    );
};

export default UserIdInput;
