import React, { useEffect, useState } from 'react';

interface OCRProps {
    onDataLoaded: (data: any) => void;
}

const OCR: React.FC<OCRProps> = ({ onDataLoaded }) => {
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await fetch('/mockup/ocrmockup.json');
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                const mockData = await response.json();
                console.log('Fetched OCR Data:', mockData); // 데이터 확인용 로그
                sessionStorage.setItem('ocrData', JSON.stringify(mockData));
                onDataLoaded(mockData);
            } catch (error) {
                console.error('Error fetching OCR data:', error);
                setError('서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.');
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [onDataLoaded]);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>{error}</div>;
    }

    return null;
};

export default OCR;