import React, { useEffect, useState, useRef } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';

interface Inquiry {
    id: number;
    title: string;
    content: string;
    image?: string | null;
    status: string;
}

const InquiryList: React.FC = () => {
    const [inquiries, setInquiries] = useState<Inquiry[]>([]);
    const isFetching = useRef(false); // 요청 중인지 여부를 추적하는 ref 추가

    useEffect(() => {
        const fetchInquiries = async () => {
            if (isFetching.current) return; // 이미 요청 중이면 중복 요청 방지
            isFetching.current = true; // 요청 시작

            try {
                const authToken = localStorage.getItem('authToken');
                const refreshToken = localStorage.getItem('refreshToken');
                const userId = localStorage.getItem('id');

                if (!authToken || !refreshToken) {
                    console.error('인증 토큰이 없습니다.');
                    isFetching.current = false; // 요청 종료
                    return;
                }

                const fetchData = async (token: string) => {
                    return await axios.post('/board/posts', {}, {
                        headers: {
                            'Authorization': `Bearer ${token}`,
                            'Content-Type': 'application/json'
                        }
                    });
                };

                let response;
                try {
                    response = await fetchData(authToken);
                } catch (error: any) {
                    if (error.response && error.response.status === 401) {
                        try {
                            const refreshResponse = await axios.post('/refresh', { refreshToken, userId }, {
                                headers: {
                                    'Content-Type': 'application/json'
                                }
                            });
                            console.log(refreshResponse);
                            const newAccessToken = refreshResponse.data.data.accessToken;
                            localStorage.setItem('authToken', newAccessToken);
                            response = await fetchData(newAccessToken);
                        } catch (refreshError) {
                            console.error('토큰 갱신 요청 중 오류:', refreshError);
                            isFetching.current = false; // 요청 종료
                            return;
                        }
                    } else {
                        throw error;
                    }
                }

                setInquiries(response.data.data);
            } catch (error) {
                console.error('문의사항을 가져오는 중 오류가 발생했습니다:', error);
            } finally {
                isFetching.current = false; // 요청 종료
            }
        };
        fetchInquiries();
    }, []);

    const getStatusColor = (status: string) => {
        switch (status) {
            case '답변완료':
                return 'text-green-500';
            case '미확인':
                return 'text-red-500';
            case '확인 중':
                return 'text-yellow-500';
            default:
                return 'text-gray-500';
        }
    };

    return (
        <div className="p-4">
            <h2 className="text-2xl font-bold mb-4">나의 문의사항 목록</h2>
            <ul className="space-y-4">
                {inquiries.length > 0 ? (
                    inquiries.map((inquiry) => (
                        <li key={inquiry.id} className="p-4 border rounded-lg shadow-md bg-white">
                            <Link to={`/servicecenter/list/${inquiry.id}`}>
                                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-2">
                                    <span className="text-lg font-semibold">{inquiry.title}</span>
                                    <span className={`text-sm ${getStatusColor(inquiry.status)} mt-2 sm:mt-0`}>{inquiry.status}</span>
                                </div>
                                <p className="text-gray-700">{inquiry.content}</p>
                            </Link>
                        </li>
                    ))
                ) : (
                    <li className="p-4 border rounded-lg shadow-md bg-white">
                        <p className="text-gray-700">문의사항이 없습니다.</p>
                    </li>
                )}
            </ul>
        </div>
    );
};

export default InquiryList;
