import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

interface OCRProps {
    onDataLoaded: (data: any) => void;
}

const OCR: React.FC<OCRProps> = ({ onDataLoaded }) => {
    const [loading, setLoading] = useState(true);
    const location = useLocation();
    const navigate = useNavigate();
    const filePath = location.state?.filePath || sessionStorage.getItem('filePath');

    useEffect(() => {
        if (!filePath) {
            navigate('/Application');
            return;
        }

        const fetchData = async () => {
            try {
                const delay = Math.floor(Math.random() * 7) + 4; // 실제 OCR의 딜레이를 구현
                await new Promise(resolve => setTimeout(resolve, delay * 1000)); // 딜레이 적용
                const response = await fetch('/mockup/ocrmockup.json');
                if (response.ok) {
                    const data = await response.json();
                    data.filePath = filePath; // 파일 경로를 데이터에 추가
                    onDataLoaded(data);
                    setLoading(false);
                } else {
                    throw new Error('Network response was not ok');
                }
            } catch (error) {
                console.error('Error fetching data:', error);
                setLoading(false); // 로딩 상태를 false로 설정하여 로딩 스피너를 숨깁니다.
            }
        };

        fetchData();
    }, [filePath, navigate, onDataLoaded]);

    if (loading) {
        return (
            <div className="fixed inset-0 bg-gray-800 bg-opacity-50 flex justify-center items-center">
                <div className="flex flex-col justify-center items-center">
                    <div className="w-32 h-32 flex items-center justify-center">
                        <div className="w-24 h-24 border-4 border-gray-300 border-t-4 border-t-gray-800 rounded-full animate-spin"></div>
                    </div>
                    <p className="text-white text-xl">잠시만 기다려주세요...</p>
                </div>
            </div>
        );
    }

    return null;
};

export default OCR;