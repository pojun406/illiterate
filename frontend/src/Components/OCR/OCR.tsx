import React, { useEffect, useState } from 'react';
import fetchWithAuth from '../AccessToken/AccessToken';

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

                const id = localStorage.getItem('id');
                if (!id) {
                    throw new Error('No id found in localStorage');
                }

                const formData = new FormData();
                formData.append('file', file);
                formData.append('id', id);

                const response = await fetchWithAuth('/ocr/file', {
                    method: 'POST',
                    body: formData,
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
                    }
                });

                if (typeof response === 'string') {
                    throw new Error(response);
                }

                onDataLoaded(response.data);

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