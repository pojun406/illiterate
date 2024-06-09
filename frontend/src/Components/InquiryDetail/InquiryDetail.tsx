import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import Sidebar from '../Sidebar/Sidebar';

interface Inquiry {
    title: string;
    content: string;
    image?: string | null;
    answer?: string | null;
}

const InquiryDetail = () => {
    const { id } = useParams<{ id: string }>();
    const [inquiry, setInquiry] = useState<Inquiry | null>(null);

    useEffect(() => {
        fetch('/mockup/service.json')
            .then(response => response.json())
            .then(data => {
                if (id) {
                    const foundInquiry = data.find((item: Inquiry, index: number) => index === parseInt(id));
                    setInquiry(foundInquiry);
                }
            })
            .catch(error => console.error('Error fetching inquiry:', error));
    }, [id]);

    if (!inquiry) {
        return <div>문의 사항을 찾을 수 없습니다.</div>;
    }

    return (
        <div className="flex">
            <Sidebar />
            <div className="flex-1 p-8">
                <div className="p-8 border-2 border-gray-300 rounded-lg shadow-md bg-white">
                    <div className="mb-6">
                        <label className="block text-lg font-medium text-gray-700 mb-2">제목</label>
                        <input 
                            type="text" 
                            value={inquiry.title} 
                            readOnly 
                            tabIndex={-1}
                            onMouseDown={(e) => e.preventDefault()}
                            className="w-full p-2 border border-gray-300 rounded-md bg-gray-100 cursor-default"
                        />
                    </div>
                    <div className="mb-6">
                        <label className="block text-lg font-medium text-gray-700 mb-2">내용</label>
                        <textarea 
                            value={inquiry.content} 
                            readOnly 
                            tabIndex={-1}
                            onMouseDown={(e) => e.preventDefault()}
                            className="w-full p-2 border border-gray-300 rounded-md bg-gray-100 cursor-default resize-none"
                            rows={4}
                        />
                    </div>
                    {inquiry.image && (
                        <div className="mb-6">
                            <label className="block text-lg font-medium text-gray-700 mb-2">첨부 이미지</label>
                            <img 
                                src={inquiry.image} 
                                alt="첨부 이미지" 
                                className="w-3/4 h-auto rounded-md border border-gray-300 mx-auto"
                            />
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default InquiryDetail;