import React, { useEffect, useState, useRef } from 'react';
import { Link } from 'react-router-dom';
import fetchWithAuth from '../AccessToken/AccessToken';

interface Inquiry {
    id: number;
    title: string;
    content: string;
    image?: string | null;
    status: string;
}

const InquiryList: React.FC = () => {
    const [inquiries, setInquiries] = useState<Inquiry[]>([]);
    const didFetchRef = useRef(false);

    useEffect(() => {
        if (didFetchRef.current) return;
        didFetchRef.current = true;

        const fetchInquiries = async () => {
            try {
                const response = await fetchWithAuth('/posts');
                if (typeof response !== 'string') {
                    setInquiries(response.data.data);
                } else {
                    console.error('문의사항을 가져오는 중 오류가 발생했습니다:', response);
                }
            } catch (error) {
                console.error('문의사항을 가져오는 중 오류가 발생했습니다:', error);
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
