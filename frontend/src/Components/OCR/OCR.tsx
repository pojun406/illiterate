import React, { useEffect, useState } from 'react';

interface OCRProps {
    onDataLoaded: (data: any) => void;
}

const OCR: React.FC<OCRProps> = ({ onDataLoaded }) => {
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await fetch('/mockup/customerSupport.json');
                const mockData = await response.json();
                sessionStorage.setItem('ocrData', JSON.stringify(mockData));
                onDataLoaded(mockData);
            } catch (error) {
                console.error('Error fetching OCR data:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [onDataLoaded]);

    if (loading) {
        return <div>Loading...</div>;
    }

    return null;
};

export default OCR;