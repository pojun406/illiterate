import React, { useEffect, useState, useRef } from 'react';
import axios from 'axios';

interface OCRProps {
    onDataLoaded: (data: any) => void;
    file: File | null;
}

const OCR: React.FC<OCRProps> = ({ onDataLoaded, file }) => {
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const isFetching = useRef(false); // 요청 중인지 여부를 추적하는 ref 추가

    useEffect(() => {
        const fetchData = async () => {
            if (isFetching.current) return; // 이미 요청 중이면 중복 요청 방지
            isFetching.current = true; // 요청 시작

            try {
                if (!file) {
                    throw new Error('No file provided');
                }

                const sendRequest = async (token: string) => {
                    const response = await axios.post('/ocr/file', file, {
                        headers: {
                            'Authorization': `Bearer ${token}`,
                            'Content-Type': 'multipart/form-data'
                        }
                    });
                    console.log('OCR 요청 결과:', response);
                    return response;
                };

                let response;
                try {
                    response = await sendRequest(localStorage.getItem('accessToken') || '');
                } catch (error: any) {
                    if (error.response && error.response.status === 401) {
                        try {
                            const refreshToken = localStorage.getItem('refreshToken');
                            const userId = localStorage.getItem('id');
                            const refreshResponse = await axios.post('/refresh', { refreshToken, userId }, {
                                headers: {
                                    'Content-Type': 'application/json'
                                }
                            });
                            console.log('토큰 갱신 요청 결과:', refreshResponse);
                            const newAccessToken = refreshResponse.data.data.accessToken;
                            localStorage.setItem('accessToken', newAccessToken);
                            response = await sendRequest(newAccessToken);
                        } catch (refreshError) {
                            console.error('토큰 갱신 요청 중 오류:', refreshError);
                            setError('서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.');
                            isFetching.current = false; // 요청 종료
                            return;
                        }
                    } else {
                        throw error;
                    }
                }

                onDataLoaded(response.data);

            } catch (error) {
                console.error('Error fetching OCR data:', error);
                setError('서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.');
            } finally {
                setLoading(false);
                isFetching.current = false; // 요청 종료
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