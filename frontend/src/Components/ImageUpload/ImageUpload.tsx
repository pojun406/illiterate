import React, { useState, useRef, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import fetchWithAuth from '../AccessToken/AccessToken';

const ImageUpload: React.FC = () => {
    const [file, setFile] = useState<File | null>(null);
    const [filePath, setFilePath] = useState<string | null>(null);
    const [showModal, setShowModal] = useState<boolean>(false);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const fileInputRef = useRef<HTMLInputElement | null>(null);
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        setShowModal(false);
    }, [location.pathname]);

    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.files && event.target.files[0]) {
            const selectedFile = event.target.files[0];
            setFile(selectedFile);
            setFilePath(URL.createObjectURL(selectedFile));
            setShowModal(true);
        }
    };

    const handleDragOver = (event: React.DragEvent<HTMLDivElement>) => {
        event.preventDefault();
    };

    const handleDrop = (event: React.DragEvent<HTMLDivElement>) => {
        event.preventDefault();
        if (event.dataTransfer.files && event.dataTransfer.files[0]) {
            const droppedFile = event.dataTransfer.files[0];
            setFile(droppedFile);
            setFilePath(URL.createObjectURL(droppedFile));
            setShowModal(true);
        }
    };

    const handleRemoveImage = () => {
        setFile(null);
        setFilePath(null);
        if (fileInputRef.current) {
            fileInputRef.current.value = '';
        }
    };

    const handleConfirm = async () => {
        if (file) {
            setShowModal(false);
            setIsLoading(true);
            try {
                const formData = new FormData();
                formData.append('file', file);

                const response = await fetchWithAuth('/ocr/upload', null, formData);
                
                if (typeof response !== 'string') {
                    const data = response.data;
                    console.log('OCR 결과:', data);

                    if (data && data.data && typeof data.data.ocrResult === 'string') {
                        try {
                            const parsedOcrResult = JSON.parse(data.data.ocrResult);
                            const ocrId = data.data.ocrId;
                            const originalImg = data.data.originalImg;
                            console.log('파싱된 OCR 결과:', parsedOcrResult);
                            console.log('OCR ID:', ocrId);
                            console.log('originalImg:', originalImg);
                            navigate('/result', { state: { fromImageUpload: true, file: file, filePath1: filePath, ocrResult: parsedOcrResult, ocrId: ocrId, originalImg: originalImg} });
                        } catch (error) {
                            console.error('OCR 결과 JSON 파싱 오류:', error);
                        }
                    } else {
                        console.error('OCR 결과가 유효하지 않습니다.');
                    }
                } else {
                    console.error('OCR 요청 실패:', response);
                }
            } catch (error) {
                console.error('OCR 요청 오류:', error);
            } finally {
                setIsLoading(false);
            }
        } else {
            console.error('파일이 선택되지 않았습니다.');
        }
    };

    const handleCancel = () => {
        handleRemoveImage();
        setShowModal(false);
    };

    return (
        <div className="w-full">
            {isLoading ? (
                <div className="flex justify-center items-center h-screen">
                    <p className="text-lg text-gray-500">로딩 중...</p>
                </div>
            ) : (
                <>
                    {!filePath && (
                        <div>
                            <div
                                onDragOver={handleDragOver}
                                onDrop={handleDrop}
                                onClick={() => fileInputRef.current?.click()}
                                className="p-6 border-2 border-dashed border-gray-400 flex justify-center items-center cursor-pointer min-h-[688px] w-full">
                                <p className="text-gray-600 text-center text-lg">이미지를 드래그하거나 클릭하여 업로드하세요.</p>
                                <input
                                    type="file"
                                    ref={fileInputRef}
                                    onChange={handleFileChange}
                                    className="hidden"
                                />
                            </div>
                        </div>
                    )}
                    {showModal && (
                        <div className="fixed inset-0 bg-gray-800 bg-opacity-75 flex justify-center items-center z-50">
                            <div className="bg-white p-8 rounded-lg shadow-2xl w-3/4">
                                <div className="border-2 border-gray-300 p-4 rounded-lg ">
                                    <p className="text-lg font-semibold mb-4">해당 이미지가 맞습니까?</p>
                                    <div className="flex justify-center items-center w-full mb-4 overflow-auto">
                                        <div style={{ maxHeight: '70vh', overflowY: 'auto' }}>
                                            <img src={filePath || undefined} alt="Selected" className="rounded-lg shadow-md w-full"/>
                                        </div>
                                    </div>
                                    <div className="flex justify-center space-x-20 w-full">
                                        <button
                                            onClick={handleCancel}
                                            className="bg-red-500 text-white py-2 px-4 rounded-lg hover:bg-red-600 transition duration-200 w-1/3"
                                        >
                                            취소
                                        </button>
                                        <button
                                            onClick={handleConfirm}
                                            className="bg-green-500 text-white py-2 px-4 rounded-lg hover:bg-green-600 transition duration-200 w-1/3"
                                        >
                                            확인
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}
                </>
            )}
        </div>
    );
};

export default ImageUpload;