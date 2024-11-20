import fetchWithAuth from '../../Components/AccessToken/AccessToken';
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

interface Post {
    boardIdx: number;
    title: string;
    createdAt: string;
    status: string;
    userId: string | null;
}

const Service = () => {
    const navigate = useNavigate();
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [file, setFile] = useState<File | null>(null);



    const handleSubmit = async () => {
        const formData = new FormData();
        formData.append('title', title);
        formData.append('content', content);
        if (file) {
            formData.append('image', file);
        }

        try {
            const response = await fetchWithAuth('/board/post', formData);
            if (typeof response === 'string') {
                return;
            }
            if (response?.data?.status === 'SUCCESS') {
                alert('문의가 등록되었습니다.');
                navigate('/servicecenter');
            }
        } catch (error) {
            console.error('문의 등록 실패:', error);
            alert('문의 등록에 실패했습니다.');
        }
    }


    return (
        <div className='p-6 max-w-[1260px] mx-auto'>
            <div className='flex justify-between items-center'>
                <h1 className='text-2xl font-bold mb-6'>문의하기</h1>
                <button className='bg-blue-500 text-white px-4 py-2 rounded-md mb-4' onClick={() => navigate('/servicecenter')}>목록으로</button>
            </div>
            <div className='flex flex-col gap-4'>
                <div className='flex flex-col gap-2'>
                    <label htmlFor='title'>제목</label>
                    <input type='text' id='title' className='border border-gray-300 rounded-md p-2' onChange={(e) => setTitle(e.target.value)} />
                </div>
                <div className='flex flex-col gap-2'>
                    <label htmlFor='content'>내용</label>
                    <textarea id='content' className='border border-gray-300 rounded-md p-2' onChange={(e) => setContent(e.target.value)} /> 
                </div>
                <input type='file' id='file' className='border border-gray-300 rounded-md p-2' onChange={(e) => setFile(e.target.files?.[0] || null)}/>
                <button className='bg-blue-500 text-white px-4 py-2 rounded-md' onClick={handleSubmit}>문의하기</button>
            </div>
        </div>
    );
};

export default Service;
