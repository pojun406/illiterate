import React, { useState } from "react";
import { PiUserListLight } from "react-icons/pi";

interface UsernameInputProps {
    onUsernameChange: (username: string) => void;
}

const UserNameInput: React.FC<UsernameInputProps> = ({ onUsernameChange }) => {
    const [userName, setUsername] = useState("");

    const handleUsernameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setUsername(e.target.value);
        onUsernameChange(e.target.value);
    };

    return (
        <div className="flex items-center mb-4 border rounded-md focus-within:border-black focus-within:text-black">
            <PiUserListLight className="text-gray-400 h-6 w-6 ml-2 focus-within:text-black" />
            <input
                type="text"
                placeholder="이름"
                value={userName}
                onChange={handleUsernameChange}
                className="flex-1 px-4 py-2 ml-2 focus:outline-none rounded-l-md rounded-r-md"
            />
        </div>
    );
};

export default UserNameInput;
