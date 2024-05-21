import React, { useState } from "react";
import { AiOutlineUser } from "react-icons/ai";
import "../../Styles/UserIdInput.css"; // CSS 파일을 import 합니다.

interface UserIdInputProps {
    onUseridChange: (userid: string) => void;
    UseridValid: (isValid: boolean) => void;
}

const UserIdInput: React.FC<UserIdInputProps> = ({ onUseridChange, UseridValid }) => {
    const [userId, setUserId] = useState("");
    const [isUseridValid, setIsUseridValid] = useState(true);

    const handleCheckUserid = () => {
        setIsUseridValid(true);
        UseridValid(true);
    };

    const handleUseridChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setUserId(e.target.value);
        onUseridChange(e.target.value);
        setIsUseridValid(true);
    };

    return (
        <div className="mb-4">
            <div className="user-id-container flex flex-col sm:flex-row items-center">
                <div
                    className="flex items-center flex-1 min-w-0 mb-2 sm:mb-0 sm:mr-2 border rounded-md focus-within:border-black focus-within:text-black">
                    <AiOutlineUser className="text-gray-400 h-6 w-6 ml-2 focus-within:text-black"/>
                    <input
                        type="text"
                        placeholder="아이디"
                        value={userId}
                        onChange={handleUseridChange}
                        className="flex-1 px-4 py-2 ml-2 focus:outline-none rounded-l-md rounded-r-md"
                    />
                </div>
                <button
                    type="button"
                    onClick={handleCheckUserid}
                    className="user-id-button w-full sm:w-auto px-4 py-2 bg-gray-300 rounded-md hover:bg-gray-400 focus:outline-none"
                >
                    중복확인
                </button>
            </div>
            {!isUseridValid && (
                <div className="text-red-500 mt-2">이미 사용 중인 아이디입니다.</div>
            )}
        </div>
    );
};

export default UserIdInput;
