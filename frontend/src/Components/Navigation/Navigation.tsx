import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Logo from '../Logo/Logo';

const Navigation = () => {
    const [isLogin, setIsLogin] = useState(false);
    const accessToken = localStorage.getItem('authToken');

    useEffect(() => {
        if (accessToken) {
            setIsLogin(true);
        }
    }, [accessToken]);

    const handleLogout = () => {
        localStorage.removeItem('authToken');
        setIsLogin(false);
        window.location.href = '/';
    };
    return (
        <div className="flex flex-col justify-center items-center p-4">
            <div className="flex items-center w-[1260px] justify-between">
                <Link to="/">
                    <Logo />
                </Link>
                {!isLogin ? (
                    <div className="flex items-center">
                        <Link to="/auth/login" className="ml-4">로그인</Link>
                    <Link to="/auth/signup" className="ml-4">회원가입</Link>
                </div>) : (
                    <div className="flex items-center">
                        <Link to="/profile" className="ml-4">마이페이지</Link>
                        <Link to="/" className="ml-4" onClick={handleLogout}>로그아웃</Link>
                    </div>
                )}
            </div>
            <div className="flex flex-col items-center w-[1260px] mt-4">
                <div className="flex items-center w-full">
                    <Link to="/application" className="text-center mx-4">문서등록</Link>
                    <Link to="/mydocument" className="text-center mx-4">문서목록</Link>
                    <Link to="/servicecenter" className="text-center mx-4">고객센터</Link>
                </div>
            </div>
        </div>
    );
};

export default Navigation;