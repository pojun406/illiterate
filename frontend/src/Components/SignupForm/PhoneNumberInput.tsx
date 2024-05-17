import React, { useState } from "react";
import { FaBroadcastTower } from "react-icons/fa";
import { AiOutlinePhone, AiOutlineLock } from "react-icons/ai";

interface PhoneNumberInputProps {
    onPhoneNumberChange: (phoneNumber: string) => void;
    onVerificationCodeSend: () => void;
    isCodeSent: boolean;
    onVerificationCodeChange: (verificationCode: string) => void;
    onSelectCarrier: (carrier: string) => void; // New prop for handling carrier selection
}

const PhoneNumberInput: React.FC<PhoneNumberInputProps> = ({
                                                               onPhoneNumberChange,
                                                               onVerificationCodeSend,
                                                               isCodeSent,
                                                               onVerificationCodeChange,
                                                               onSelectCarrier, // Destructure onSelectCarrier from props
                                                           }) => {
    const [selectedCarrier, setSelectedCarrier] = useState("통신사를 선택해주세요.");
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const [phoneNumber, setPhoneNumber] = useState("");
    const [verificationCode, setVerificationCode] = useState("");

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

    const handleCarrierChange = (carrier: string): void => {
        setSelectedCarrier(carrier);
        onSelectCarrier(carrier); // Call onSelectCarrier with the selected carrier
        setIsDropdownOpen(false);
    };

    const toggleDropdown = (): void => {
        setIsDropdownOpen(!isDropdownOpen);
    };

    return (
        <>
            <div className="flex items-center mb-4 border rounded-md focus-within:border-black focus-within:text-black relative">
                <FaBroadcastTower className="text-gray-400 h-6 w-6 ml-2 focus-within:text-black" />
                <div
                    className={`flex-1 px-4 py-2 ml-2 focus:outline-none rounded-r-md cursor-pointer ${selectedCarrier === "통신사를 선택해주세요." ? "text-gray-400" : ""}`}
                    onClick={toggleDropdown}
                >
                    {selectedCarrier}
                </div>
                {isDropdownOpen && (
                    <ul className="absolute top-full left-0 w-full bg-white border rounded-md mt-1 grid grid-cols-2">
                        <li className="p-1 hover:bg-gray-200 cursor-pointer" onClick={() => handleCarrierChange("SKT")}>
                            SKT
                        </li>
                        <li className="p-1 hover:bg-gray-200 cursor-pointer"
                            onClick={() => handleCarrierChange("SKT 알뜰폰")}>
                            SKT 알뜰폰
                        </li>
                        <li className="p-1 hover:bg-gray-200 cursor-pointer" onClick={() => handleCarrierChange("KT")}>
                            KT
                        </li>
                        <li className="p-1 hover:bg-gray-200 cursor-pointer"
                            onClick={() => handleCarrierChange("KT 알뜰폰")}>
                            KT 알뜰폰
                        </li>
                        <li className="p-1 hover:bg-gray-200 cursor-pointer"
                            onClick={() => handleCarrierChange("LGU+")}>
                            LGU+
                        </li>
                        <li className="p-1 hover:bg-gray-200 cursor-pointer"
                            onClick={() => handleCarrierChange("LGU+ 알뜰폰")}>
                            LGU+ 알뜰폰
                        </li>
                    </ul>
                )}
            </div>
            <div className="flex items-center mb-4 border rounded-md focus-within:border-black focus-within:text-black">
                <AiOutlinePhone className="text-gray-400 h-6 w-6 ml-2 focus-within:text-black" />
                <input
                    type="text"
                    placeholder="전화번호"
                    value={phoneNumber}
                    onChange={handlePhoneNumberChange}
                    className="flex-1 px-4 py-2 ml-2 focus:outline-none rounded-r-md"
                />
                <button
                    onClick={onVerificationCodeSend}
                    className="px-4 py-2 ml-2 bg-gray-300 rounded-md hover:bg-gray-400 focus:outline-none"
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
                        onChange={(e) => setVerificationCode(e.target.value)}
                        className="block w-full px-4 py-2 ml-2 focus:outline-none"
                    />
                </div>
            )}
        </>
    );
};

export default PhoneNumberInput;
