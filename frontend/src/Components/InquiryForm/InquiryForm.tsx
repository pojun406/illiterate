import React, { useState } from 'react';

const InquiryForm = () => {
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [image, setImage] = useState<File | null>(null);
    const [imagePreview, setImagePreview] = useState<string | null>(null);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        // 문의 사항 저장 로직 추가
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
            <button type="submit" className="w-full px-4 py-2 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none focus:ring-2 focus:ring-blue-500">제출</button>
        </form>
    );
};

export default InquiryForm;