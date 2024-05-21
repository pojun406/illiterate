import React, { useState } from "react";
import { Link } from "react-router-dom";
import Logo from "../../Components/Logo/Logo";
import LoginForm from "../../Components/LoginForm/LoginForm";

const Login = () => {
    const [activeTab, setActiveTab] = useState("basic");

    return (
        <div className="flex min-h-screen items-center justify-center px-4">
            <div className="w-full max-w-xs sm:max-w-sm md:max-w-md lg:max-w-lg mx-auto">
                <div className="flex items-center justify-center my-4">
                    <Link to="/">
                        <Logo />
                    </Link>
                </div>
                <LoginForm />
            </div>
        </div>
    );
};

export default Login;