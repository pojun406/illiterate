import React, { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom'; 
import axios from 'axios';

const ImageUpload: React.FC = () => {
    const [file, setFile] = useState<File | null>(null);
    const [filePath, setFilePath] = useState<string | null>(null);
    const [showModal, setShowModal] = useState<boolean>(false);
    const fileInputRef = useRef<HTMLInputElement | null>(null);
    const navigate = useNavigate(); // useNavigate hook 사용

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

    const handleConfirm = () => {
        setShowModal(false);
        navigate('/OCR', { state: { filePath: filePath } });
    };

    const handleCancel = () => {
        handleRemoveImage();
        setShowModal(false);
    };

    return (
        <div className="p-4 flex flex-col justify-center items-center sm:px-12 md:px-24 lg:px-48 xl:px-96">
            {!filePath ? (
                <div className="min-w-[280px] w-full sm:min-w-[360px]">
                    <div
                        onDragOver={handleDragOver}
                        onDrop={handleDrop}
                        onClick={() => fileInputRef.current?.click()}
                        className="p-6 border-2 border-dashed border-gray-400 flex justify-center items-center cursor-pointer min-h-[40vh] sm:min-h-[50vh]"
                    >
                        <p className="text-gray-600 text-center text-lg">이미지를 드래그하거나 클릭하여 업로드하세요.</p>
                        <input
                            type="file"
                            ref={fileInputRef}
                            onChange={handleFileChange}
                            className="hidden"
                        />
                    </div>
                </div>
            ) : (
                <div className="p-4 flex flex-col justify-center items-center">
                    <div className="mt-4 w-full text-center">
                        <p className="mt-2">File Path: {filePath}</p>
                        <div className="mt-4 flex flex-col space-y-2">
                            <button
                                className="bg-yellow-500 text-white py-2 px-4 rounded hover:bg-yellow-600"
                            >
                                OCR하기
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {showModal && (
                <div className="fixed inset-0 bg-gray-800 bg-opacity-75 flex justify-center items-center z-50">
                    <div className="bg-white p-8 rounded-lg shadow-2xl max-w-lg w-full">
                        <div className="border-2 border-gray-300 p-4 rounded-lg">
                            <p className="text-lg font-semibold mb-4">해당 이미지가 맞습니까?</p>
                            <div className="flex justify-between items-center w-full mb-4">
                                <img src={filePath || undefined} alt="Selected" className="rounded-lg shadow-md" style={{ maxWidth: 'calc(100% - 20px)' }} />
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
        </div>
    );
};

export default ImageUpload;