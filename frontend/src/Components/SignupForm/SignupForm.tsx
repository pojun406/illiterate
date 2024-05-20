import React, { useState } from "react";
import UserIdInput from "./UserIdInput";
import PasswordInput from "./PasswordInput";
import PhoneNumberInput from "./PhoneNumberInput";

const SignupForm: React.FC = () => {
    const [username, setUsername] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [phoneNumber, setPhoneNumber] = useState<string>("");
    const [verificationCode, setVerificationCode] = useState<string>("");
    const [isCodeSent, setIsCodeSent] = useState<boolean>(false);
    const [isUsernameValid, setIsUsernameValid] = useState<boolean>(true);
    const [selectedCarrier, setSelectedCarrier] = useState<string>("");

    const handlePhoneNumberChange = (phoneNumber: string): void => {
        setPhoneNumber(phoneNumber);
    };

    const handleVerificationCodeSend = (): void => {
        setIsCodeSent(true);
    };

    const handleVerificationCodeChange = (code: string): void => {
        setVerificationCode(code);
    };

    const handleUsernameChange = (username: string): void => {
        setUsername(username);
    };

    const handleUsernameValidation = (isValid: boolean): void => {
        setIsUsernameValid(isValid);
    };

    const handleSelectCarrier = (carrier: string): void => {
        setSelectedCarrier(carrier);
    };

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>): void => {
        e.preventDefault();
        console.log("Username:", username);
        console.log("Password:", password);
        console.log("Phone Number:", phoneNumber);
        console.log("Verification Code:", verificationCode);
        console.log("Selected Carrier:", selectedCarrier);
    };

    return (
        <div>
            <div className="border rounded-md overflow-hidden w-[500px]">
                <div className="px-4 py-6">
                    <div className="text-center font-bold text-blue-300 text-xl mb-4">회원가입</div>
                    <form onSubmit={handleSubmit}>
                        <UserIdInput
                            onUsernameChange={handleUsernameChange}
                            UsernameValid={handleUsernameValidation}
                        />
                        <PasswordInput setPassword={setPassword} />
                        <PhoneNumberInput
                            onPhoneNumberChange={handlePhoneNumberChange}
                            onVerificationCodeSend={handleVerificationCodeSend}
                            isCodeSent={isCodeSent}
                            onVerificationCodeChange={handleVerificationCodeChange}
                            onSelectCarrier={handleSelectCarrier} // Pass handleSelectCarrier function
                        />
                        <button
                            type="submit"
                            className="w-full mt-4 px-4 py-2 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none"
                        >
                            회원가입
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default SignupForm;
