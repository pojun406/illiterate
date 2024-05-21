import React, { useState } from "react";
import { AiOutlinePhone, AiOutlineLock } from "react-icons/ai";
import "../../Styles/PhoneNumberInput.css"; // CSS 파일을 import 합니다.

interface PhoneNumberInputProps {
    onPhoneNumberChange: (phoneNumber: string) => void;
    onVerificationCodeSend: (isCodeSent: boolean) => void;
    onVerificationCodeChange: (verificationCode: string) => void;
    onSelectCarrier: (carrier: string) => void;
}

const PhoneNumberInput: React.FC<PhoneNumberInputProps> = ({
                                                               onPhoneNumberChange,
                                                               onVerificationCodeSend,
                                                               onVerificationCodeChange,
                                                               onSelectCarrier,
                                                           }) => {
    const [selectedCarrier, setSelectedCarrier] = useState("통신사를 선택해주세요.");
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const [phoneNumber, setPhoneNumber] = useState("");
    const [verificationCode, setVerificationCode] = useState("");
    const [isCodeSent, setIsCodeSent] = useState(false);

    const handlePhoneNumberChange = (e: React.ChangeEvent<HTMLInputElement>): void => {
        let input = e.target.value.replace(/\D/g, "");
        if (input.length > 11) {
            input = input.slice(0, 11);
        }
        const formattedPhoneNumber = formatPhoneNumber(input);
        setPhoneNumber(formattedPhoneNumber);
        onPhoneNumberChange(formattedPhoneNumber);
    };

    const formatPhoneNumber = (input: string): string => {
        const part1 = input.slice(0, 3);
        const part2 = input.slice(3, 7);
        const part3 = input.slice(7, 11);
        if (input.length <= 3) {
            return part1;
        } else if (input.length <= 7) {
            return `${part1}-${part2}`;
        } else {
            return `${part1}-${part2}-${part3}`;
        }
    };

    const handleCodesend = () => {
        setIsCodeSent(!isCodeSent);
        onVerificationCodeSend(!isCodeSent);
    };

    const handleVerificationCodeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setVerificationCode(e.target.value);
        onVerificationCodeChange(e.target.value);
    };

    return (
        <>
            <div className="phone-number-container flex flex-col sm:flex-row items-center mb-4">
                <div className="flex items-center flex-1 min-w-0 mb-2 sm:mb-0 sm:mr-2 border rounded-md focus-within:border-black focus-within:text-black">
                    <AiOutlinePhone className="text-gray-400 h-6 w-6 ml-2 focus-within:text-black" />
                    <input
                        type="text"
                        placeholder="전화번호"
                        value={phoneNumber}
                        onChange={handlePhoneNumberChange}
                        className="flex-1 px-4 py-2 ml-2 focus:outline-none rounded-md sm:rounded-l-md rounded-r-md"
                    />
                </div>
                <button
                    type="button"
                    onClick={handleCodesend}
                    className="phone-number-button w-full sm:w-auto px-4 py-2 bg-gray-300 rounded-md hover:bg-gray-400 focus:outline-none"
                >
                    {isCodeSent ? "재전송" : "인증"}
                </button>
            </div>
            {isCodeSent && (
                <div className="flex items-center mb-4 border rounded-md focus-within:border-black focus-within:text-black">
                    <AiOutlineLock className="text-gray-400 h-6 w-6 ml-2 focus-within:text-black" />
                    <input
                        type="text"
                        placeholder="인증코드"
                        value={verificationCode}
                        onChange={handleVerificationCodeChange}
                        className="block w-full px-4 py-2 ml-2 focus:outline-none rounded-l-md rounded-r-md"
                    />
                </div>
            )}
        </>
    );
};

export default PhoneNumberInput;
