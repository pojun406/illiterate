import React, { useState } from "react";
import UserIdInput from "./UserIdInput";
import PasswordInput from "./PasswordInput";
import PhoneNumberInput from "./PhoneNumberInput";
import UserNameInput from "./UserNameInput";

const SignupForm: React.FC = () => {
    const [userid, setUserid] = useState<string>("");
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

    const handleVerificationCodeSend = (isCodeSent: boolean): void => {
        setIsCodeSent(isCodeSent);
    };

    const handleVerificationCodeChange = (verificationCode: string): void => {
        setVerificationCode(verificationCode);
    };

    const handleUseridChange = (userid: string): void => {
        setUserid(userid);
    };
    const handleUsernameChange = (username: string): void => {
        setUsername(username);
    };

    const handleUseridValidation = (isValid: boolean): void => {
        setIsUsernameValid(isValid);
    };

    const handleSelectCarrier = (carrier: string): void => {
        setSelectedCarrier(carrier);
    };

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
        e.preventDefault();

        const data = {
            userid,
            username,
            password,
            phoneNumber
        };

        try {
            const response = await fetch('/join', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                const result = await response.json();
                console.log('Success:', result);
            } else {
                console.error('Error:', response.statusText);
            }
        } catch (error) {
            console.error('Error:', error);
        }
    };

    return (
        <div>
            <div className="border rounded-md overflow-hidden w-full max-w-xs sm:max-w-sm md:max-w-md lg:max-w-lg mx-auto">
                <div className="px-4 py-6">
                    <div className="text-center font-bold text-blue-300 text-xl mb-4">회원가입</div>
                    <form onSubmit={handleSubmit}>
                        <UserNameInput onUsernameChange={handleUsernameChange} />
                        <UserIdInput onUseridChange={handleUseridChange} UseridValid={handleUseridValidation}/>
                        <PasswordInput setPassword={setPassword} />
                        <PhoneNumberInput
                            onPhoneNumberChange={handlePhoneNumberChange}
                            onVerificationCodeSend={handleVerificationCodeSend}
                            onVerificationCodeChange={handleVerificationCodeChange}
                            onSelectCarrier={handleSelectCarrier}
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
