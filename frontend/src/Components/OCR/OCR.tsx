import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';

const OCR: React.FC = () => {
    const [loading, setLoading] = useState(true);
    const location = useLocation();
    const filePath = location.state?.filePath;

    useEffect(() => {
        const timer = setTimeout(() => {
            setLoading(false);
        }, 3000);
        return () => clearTimeout(timer);
    }, []);
    if (loading) {
        return (
            <div className="fixed inset-0 bg-gray-800 bg-opacity-50 flex justify-center items-center">
                <div className="flex flex-col justify-center items-center">
                    <svg className="animate-spin h-24 w-24" viewBox="0 0 24 24">
                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    <p className="text-white text-xl">로딩중...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="relative w-full h-full flex justify-center items-center">
            {loading ? (
                <div className="flex flex-col justify-center items-center">
                    <svg className="animate-spin h-24 w-24" viewBox="0 0 24 24">  // 크기를 h-24 w-24로 변경
                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    <p className="text-white text-xl">로딩중...</p>
                </div>
            ) : (
                <div className="p-4">
                    <p className="text-lg">파일 경로: {filePath}</p>
                </div>
            )}
        </div>
    );
};

export default OCR;