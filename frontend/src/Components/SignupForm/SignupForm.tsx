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

    const handleVerificationCodeSend = (isCodeSent:boolean): void => {
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

    const handleUsernameValidation = (isValid: boolean): void => {
        setIsUsernameValid(isValid);
    };

    const handleSelectCarrier = (carrier: string): void => {
        setSelectedCarrier(carrier);
    };

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>): void => {
        e.preventDefault();
        console.log("Userid:", userid);
        console.log("Username:", username);
        console.log("Password:", password);
        console.log("Phone Number:", phoneNumber);
        console.log("Verification Code:", verificationCode);
        console.log("Selected Carrier:", selectedCarrier);
        console.log("isUsernameValid:", isUsernameValid);
    };
    const handleTest = () =>{
        console.log("Userid:", userid);
    }

    return (
        <div>
            <div className="border rounded-md overflow-hidden w-[500px]">
                <div className="px-4 py-6">
                    <div className="text-center font-bold text-blue-300 text-xl mb-4">회원가입</div>
                    <form onSubmit={handleSubmit}>
                        <UserNameInput
                            onUsernameChange={handleUsernameChange}/>
                        <UserIdInput
                            onUseridChange={handleUseridChange}
                            UseridValid={handleUsernameValidation}
                        />
                        <PasswordInput setPassword={setPassword} />
                        <PhoneNumberInput
                            onPhoneNumberChange={handlePhoneNumberChange}
                            onVerificationCodeSend={handleVerificationCodeSend}
                            onVerificationCodeChange={handleVerificationCodeChange}
                            onSelectCarrier={handleSelectCarrier}
                        />
                        <button
                            className="w-full mt-4 px-4 py-2 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none"
                            onClick={handleTest}
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
