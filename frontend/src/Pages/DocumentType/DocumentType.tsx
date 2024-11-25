import fetchWithAuth from '../../Components/AccessToken/AccessToken';
import React, { useState } from 'react';

const DocumentType = () => {
    const [infoTitle, setInfoTitle] = useState('');
    const handleTitleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setInfoTitle(event.target.value);
    };

    const handleImageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        console.log(event.target.files);
    };

    const handleButtonClick = async () => {
        const formData = new FormData();
        const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
        if (fileInput && fileInput.files) {
            formData.append('file', fileInput.files[0]);
            console.log('File:', fileInput.files[0]);
        }
        formData.append('infoTitle', infoTitle);
        console.log('infoTitle:', infoTitle);

        try {
            console.log('FormData Request:', formData.get('file'), formData.get('infoTitle'));
            const response = await fetchWithAuth('/admin/paperinfo', {}, formData);
            console.log('Response:', response);
        } catch (error) {
            console.error('Error:', error);
        }
    };

    return (
        <div className="flex flex-col items-center p-6 w-[1260px] mx-auto h-[77.9vh]">
            <h1 className="text-xl mb-4">문서 타입 등록 페이지</h1>
            {/* 문서 제목 입력 */}
            <input
                type="text"
                placeholder="문서 제목을 입력하세요"
                value={infoTitle}
                onChange={handleTitleChange}
                className="mb-4 px-4 py-2 border rounded w-full max-w-sm"
            />
            {/* 이미지 선택 */}
            <input
                type="file"
                accept="image/*"
                onChange={handleImageChange}
                className="mb-4"
            />
            {/* 업로드 버튼 */}
            <button
                onClick={handleButtonClick}
                className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
            >
                문서 타입 등록
            </button>
        </div>
    );
};

export default DocumentType;
