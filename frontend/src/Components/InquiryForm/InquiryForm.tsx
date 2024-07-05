import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';

const InquiryForm = () => {
    const location = useLocation();
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [image, setImage] = useState<File | null>(null);
    const [imagePreview, setImagePreview] = useState<string | null>(null);
    const [buttonText, setButtonText] = useState('문의하기');

    useEffect(() => {
        if (location.pathname === '/servicecenter/write') {
            setButtonText('문의하기');
        } else if (location.pathname === '/servicecenter/edit') {
            setButtonText('수정하기');
        }
    }, [location.pathname]);
    

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        console.log('Form submitted'); // 디버깅을 위해 추가

        if (!title) {
            alert('제목을 입력해주세요.');
            return;
        }

        if (!content) {
            alert('내용을 입력해주세요.');
            return;
        }

        const id = localStorage.getItem('id');
        if (!id) {
            alert('ID가 없습니다.');
            return;
        }

        const data = {
            title,
            content,
            id
        };

        try {
            console.log('Sending request to:', location.pathname);
            console.log('Request data:', data);

            const authToken = localStorage.getItem('authToken');
            if (!authToken) {
                alert('인증 토큰이 없습니다.');
                return;
            }

            if (location.pathname === '/servicecenter/write') {
                const formData = new FormData();
                formData.append('request', new Blob([JSON.stringify(data)], { type: 'application/json' }));
                if (image) {
                    formData.append('image', image);
                    console.log('Image file:', image);
                }

                const response = await axios.post('/post', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                        'Authorization': `Bearer ${authToken}`
                    }
                });
                console.log('Response received:', response);
                console.log('문의 사항 저장 성공:', response);
            } else if (location.pathname === '/servicecenter/edit') {
                const id = new URLSearchParams(location.search).get('id');
                if (!id) {
                    throw new Error('ID가 없습니다.');
                }

                const formData = new FormData();
                formData.append('request', new Blob([JSON.stringify(data)], { type: 'application/json' }));
                if (image) {
                    formData.append('image', image);
                    console.log('Image file:', image);
                }

                const response = await axios.post(`/fix_post/${id}`, formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                        'Authorization': `Bearer ${authToken}`
                    }
                });
                console.log('Response received:', response);
                console.log('문의 사항 수정 성공:', response);
            }
        } catch (error) {
            console.error('문의 사항 처리 중 오류 발생:', error);
            alert('문의 사항 처리 중 오류가 발생했습니다.');
        }
    };

    const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files ? e.target.files[0] : null;
        setImage(file);
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setImagePreview(reader.result as string);
            };
            reader.readAsDataURL(file);
        } else {
            setImagePreview(null);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-4 bg-white p-6 rounded-lg shadow-md">
            <h2 className="text-2xl font-bold mb-4">문의하기</h2>
            <input type="text" value={title} onChange={(e) => setTitle(e.target.value)} placeholder="제목" className="block w-full px-4 py-2 mb-4 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
            <textarea value={content} onChange={(e) => setContent(e.target.value)} placeholder="내용" className="block w-full px-4 py-2 mb-4 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" style={{ height: '200px', verticalAlign: 'top' }} />
            <div className="flex items-center space-x-4">
                <input
                    id="fileInput"
                    type="file"
                    accept="image/*"
                    onChange={handleImageChange}
                    className="hidden"
                />
                <span>{image ? image.name : '선택된 파일 없음'}</span>
                <button
                    type="button"
                    onClick={() => document.getElementById('fileInput')?.click()}
                    className="px-4 py-2 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                    파일 선택
                </button>
            </div>
            {imagePreview && <div className="flex justify-center"><img src={imagePreview} alt="첨부 이미지" className="w-1/2 h-auto mb-4" /></div>}
            <button type="submit" className="w-full px-4 py-2 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none focus:ring-2 focus:ring-blue-500">{buttonText}</button>
        </form>
    );
};

export default InquiryForm;