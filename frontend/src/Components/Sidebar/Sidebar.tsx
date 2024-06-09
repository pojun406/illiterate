import React from 'react';
import { Link, useLocation } from 'react-router-dom';

const Sidebar = () => {
    const location = useLocation();
    const isActive = (path: string) => location.pathname === path;

    return (
        <div className="w-1/6 bg-white shadow-md rounded-lg p-4 flex flex-col items-center">
            <ul className="w-full">
                <li className={`cursor-pointer py-6 text-center border-b ${isActive('/application') ? 'font-bold' : ''}`}>
                    <Link to="/application">등록하기</Link>
                </li>
                <li className={`cursor-pointer py-6 text-center border-b ${isActive('/mydocument') ? 'font-bold' : ''}`}>
                    <Link to="/mydocument">내 문서 보기</Link>
                </li>
                <li className={`cursor-pointer py-6 text-center border-b ${isActive('/servicecenter') ? 'font-bold' : ''}`}>
                    <Link to="/servicecenter">고객센터</Link>
                </li>
            </ul>
        </div>
    );
};

export default Sidebar;