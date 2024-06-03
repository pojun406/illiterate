import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const ImageUpload: React.FC = () => {
    const [file, setFile] = useState<File | null>(null);
    const [filePath, setFilePath] = useState<string | null>(null);
    const [showModal, setShowModal] = useState<boolean>(false);
    const fileInputRef = useRef<HTMLInputElement | null>(null);
    const navigate = useNavigate(); // useNavigate hook 사용

    useEffect(() => {
        const savedFile = sessionStorage.getItem('uploadedFile');
        if (savedFile) {
            const { data, type } = JSON.parse(savedFile);
            const blob = new Blob([new Uint8Array(data)], { type });
            const url = URL.createObjectURL(blob);
            setFile(blob as File);
            setFilePath(url);
        }
    }, []);

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
        sessionStorage.removeItem('uploadedFile');
    };

    const handleConfirm = () => {
        if (file) {
            const reader = new FileReader();
            reader.onload = (event) => {
                if (event.target && event.target.result) {
                    const arrayBuffer = new Uint8Array(event.target.result as ArrayBuffer);
                    let binaryString = '';
                    for (let i = 0; i < arrayBuffer.length; i++) {
                        binaryString += String.fromCharCode(arrayBuffer[i]);
                    }
                    const encodedString = btoa(binaryString);
                    // 쿠키에 파일 데이터를 저장
                    document.cookie = `imageData=${encodeURIComponent(encodedString)}; path=/; expires=${new Date(new Date().getTime() + 86400000).toUTCString()};`;
                    setShowModal(false);
                    navigate('/result', { state: { fromImageUpload: true, filePath } });
                } else {
                    console.error('파일 읽기 실패: 결과 데이터가 없습니다.');
                }
            };
            reader.onerror = (error) => {
                console.error('파일 읽기 오류:', error);
            };
            reader.readAsBinaryString(file);
        } else {
            console.error('파일이 선택되지 않았습니다.');
        }
    };

    const handleCancel = () => {
        handleRemoveImage();
        setShowModal(false);
    };

    return (
        <div className="p-4 flex flex-col justify-center items-center sm:px-12 md:px-24 lg:px-48 xl:px-96">
            {!filePath && (
                <div className="min-w-[280px] w-full sm:min-w-[360px]">
                    <div
                        onDragOver={handleDragOver}
                        onDrop={handleDrop}
                        onClick={() => fileInputRef.current?.click()}
                        className="p-6 border-2 border-dashed border-gray-400 flex justify-center items-center cursor-pointer min-h-[75vh] sm:min-h-[75vh]"
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
        </div>
    );
};

export default ImageUpload;