import React, { useEffect, useState } from 'react';
import AccessToken from '../AccessToken/AccessToken';

interface OCRProps {
    onDataLoaded: (data: any) => void;
    file: File | null;
}

const OCR: React.FC<OCRProps> = ({ onDataLoaded, file }) => {
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                if (!file) {
                    throw new Error('No file provided');
                }

                const formData = new FormData();
                formData.append('file', file);

                const response = await AccessToken('/ocr/file', {
                    method: 'POST',
                    body: formData,
                });

                if (typeof response === 'string') {
                    throw new Error(response);
                }

                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }

                const responseData = await response.json();
                onDataLoaded(responseData);

            } catch (error) {
                console.error('Error fetching OCR data:', error);
                setError('서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.');
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [onDataLoaded, file]);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>{error}</div>;
    }

    return null;
};

export default OCR;