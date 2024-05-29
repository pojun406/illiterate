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
                    <div className="w-64 bg-gray-200 rounded-full h-2.5 dark:bg-gray-700">
                        <div className="bg-blue-600 h-2.5 rounded-full" style={{ width: '100%', transition: 'width 3s' }}></div>
                    </div>
                    <p className="text-white text-xl">로딩중...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="relative w-full h-full flex justify-center items-center">
            {loading ? (
                <div className="flex flex-col justify-center items-center">
                    <div className="w-64 bg-gray-200 rounded-full h-2.5 dark:bg-gray-700">
                        <div className="bg-blue-600 h-2.5 rounded-full" style={{ width: '100%', transition: 'width 3s' }}></div>
                    </div>
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