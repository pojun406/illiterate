import React, { useState } from "react";
import { Link } from "react-router-dom";
import Logo from "../Logo/Logo";
import {FcMenu} from "react-icons/fc";
import {IoCloseSharp} from "react-icons/io5";


const Header = () => {
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const [isLoggedIn, setIsLoggedIn] = useState(false); // Simulating login state

    const toggleMenu = () => {
        setIsMenuOpen(!isMenuOpen);
    };

    return (
        <header>
            <div className="relative flex flex-col lg:flex-row items-center py-8 lg:py-[32px]">
                <div className="flex justify-between w-full lg:w-auto">
                    <Link to="/" className="flex-shrink-0">
                        <Logo />
                    </Link>
                    <div className="lg:hidden">
                        <button
                            onClick={toggleMenu}
                            className="text-gray-500 hover:text-gray-700 focus:outline-none focus:text-gray-700"
                        >
                            {isMenuOpen ? (
                                // Close button when the menu is open
                                <IoCloseSharp className="h-6 w-6" />
                            ) : (
                                // Menu button when the menu is closed
                                <FcMenu className="h-6 w-6" />
                            )}
                        </button>
                    </div>
                </div>
                {/* Mobile menu */}
                <div className={`w-full lg:hidden ${isMenuOpen ? 'block' : 'hidden'} mt-4 lg:mt-0`}>
                    <ul className="flex flex-col">
                        {isLoggedIn ? (
                            <li>
                                <Link to="/profile" className="block px-4 py-2">프로필</Link>
                            </li>
                        ) : (
                            <>
                                <li>
                                    <Link to="/auth/login" className="block px-4 py-2">로그인</Link>
                                </li>
                                <li>
                                    <Link to="/auth/signup" className="block px-4 py-2">회원가입</Link>
                                </li>
                            </>
                        )}
                        <li>
                            <Link to="/application" className="block px-4 py-2">등록하기</Link>
                        </li>
                        <li>
                            <Link to="/servicecenter" className="block px-4 py-2">고객센터</Link>
                        </li>
                    </ul>
                </div>
                {/* Desktop menu */}
                <div className="hidden lg:flex lg:items-center lg:ml-auto lg:w-auto">
                    <ul className="flex space-x-4">
                        <li>
                            <Link to="/application" className="block px-4 py-2">등록하기</Link>
                        </li>
                        <li>
                            <Link to="/servicecenter" className="block px-4 py-2">고객센터</Link>
                        </li>
                    </ul>
                    <ul className="flex space-x-4 ml-4">
                        {isLoggedIn ? (
                            <li>
                                <Link to="/profile" className="block px-4 py-2">프로필</Link>
                            </li>
                        ) : (
                            <>
                                <li>
                                    <Link to="/auth/login" className="block px-4 py-2">로그인</Link>
                                </li>
                                <li>
                                    <Link to="/auth/signup" className="block px-4 py-2">회원가입</Link>
                                </li>
                            </>
                        )}
                    </ul>
                </div>
            </div>
        </header>
    );
};

export default Header;
