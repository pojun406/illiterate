// Login.tsx

import React, { useState } from "react";
import { Link } from "react-router-dom";
import Logo from "../../Components/Logo/Logo";
import SignupForm from "../../Components/SignupForm/SignupForm";

const Signup = () => {
    const [activeTab, setActiveTab] = useState("basic");

    return (
        <div className="flex min-h-screen items-center justify-center px-4">
            <div className="flow-root">
                <div className="flex items-center justify-center my-4">
                    <Link to="/">
                        <Logo />
                    </Link>
                </div>
                <SignupForm />
            </div>
        </div>
    );
};

export default Signup;
