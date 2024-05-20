import React from "react";
import { Outlet } from "react-router-dom";
import Navigation from "./Navigation/Navigation";


const Header = () => {
    // Header 사이트의 제일 위 헤더 입니다.
    return (
        <header className="mx-auto max-w-container px-4 sm:px-6 lg:px-8">
            <Navigation />
            <hr/>
            <Outlet />
        </header>
    );
};

export default Header;
