import React, {useEffect, useState} from "react";
import { Link } from "react-router-dom";
import Logo from "../Logo/Logo";
import {FcMenu} from "react-icons/fc";
import {IoCloseSharp} from "react-icons/io5";
import AccessToken from "../AccessToken/AccessToken";

const Header = () => {
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [isDropdownOpen, setDropdownOpen] = useState(false);

    const toggleMenu = () => {
        setIsMenuOpen(!isMenuOpen);
    };

    const toggleDropdown = () => {
        setDropdownOpen(!isDropdownOpen);
    };

    const handleLogout = () =>{
        setIsLoggedIn(false);
        localStorage.clear();
    };

    useEffect(()=>{
        const message = AccessToken();
        if (message == "토큰이 없습니다."){
            setIsLoggedIn(false);
        }
        else {
            setIsLoggedIn(true);
        }
    });

    return (
        <header>
            <div className="relative flex flex-col lg:flex-row items-center py-4 lg:py-6">
                <div className="flex justify-between w-full lg:w-auto px-4 lg:px-0">
                    <Link to="/" className="flex-shrink-0">
                        <Logo/>
                    </Link>
                    <div className="lg:hidden">
                        <button
                            onClick={toggleMenu}
                            className="text-gray-500 hover:text-gray-700 focus:outline-none focus:text-gray-700"
                        >
                            {isMenuOpen ? (
                                <IoCloseSharp className="h-6 w-6"/>
                            ) : (
                                <FcMenu className="h-6 w-6"/>
                            )}
                        </button>
                    </div>
                </div>
                {/* Mobile menu */}
                <div className={`w-full lg:hidden ${isMenuOpen ? 'block' : 'hidden'} mt-4 lg:mt-0`}>
                    <ul className="flex flex-col px-4">
                        {isLoggedIn ? (
                            <>
                                <li>
                                    <Link to="/profile"
                                          className="block py-2 border-t border-b hover:bg-gray-100">닉네임</Link>
                                </li>
                                <li>
                                    <Link to="/document" className="block py-2 border-b hover:bg-gray-100">내
                                    문서 보기</Link>
                                </li>
                            </>
                        ) : (
                            <>
                                <li>
                                    <Link to="/auth/login" className="block py-2 border-t border-b hover:bg-gray-100">로그인</Link>
                                </li>
                                <li>
                                    <Link to="/auth/signup" className="block py-2 border-b hover:bg-gray-100">회원가입</Link>
                                </li>
                            </>
                        )}
                        <li>
                            <Link to="/application" className="block py-2 border-b hover:bg-gray-100">등록하기</Link>
                        </li>
                        <li>
                            <Link to="/servicecenter" className="block py-2 border-b hover:bg-gray-100">고객센터</Link>
                        </li>
                        {isLoggedIn && (<li>
                            <button onClick={handleLogout}
                                    className="w-full text-left py-2 text-red-500 hover:bg-gray-100">로그아웃
                            </button>
                        </li>)}
                    </ul>
                </div>
                {/* Desktop menu */}
                <div className="hidden lg:flex lg:items-center lg:ml-auto lg:w-auto space-x-8 px-4">
                <ul className="flex space-x-4">
                        <li>
                            <Link to="/application" className="block py-2">등록하기</Link>
                        </li>
                        <li>
                            <Link to="/servicecenter" className="block py-2">고객센터</Link>
                        </li>
                    </ul>
                    <ul className="flex space-x-4">
                        {isLoggedIn ? (
                            <li className="relative">
                                <button onClick={toggleDropdown} className="block py-2 focus:outline-none">프로필</button>
                                {isDropdownOpen && (
                                    <div
                                        className="absolute right-0 mt-2 w-48 bg-white border rounded-md shadow-lg py-1 z-20">
                                        <Link to="/profile"
                                              className="block px-4 py-2 text-gray-800 hover:bg-gray-100">닉네임</Link>
                                        <Link to="/profile" className="block px-4 py-2 text-gray-800 hover:bg-gray-100">내
                                            문서 보기</Link>
                                        <button onClick={handleLogout}
                                              className="block w-full text-left px-4 py-2 text-red-500 hover:bg-gray-100">로그아웃</button>
                                    </div>
                                )}
                            </li>
                        ) : (
                            <>
                                <li>
                                    <Link to="/auth/login" className="block py-2">로그인</Link>
                                </li>
                                <li>
                                    <Link to="/auth/signup" className="block py-2">회원가입</Link>
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
