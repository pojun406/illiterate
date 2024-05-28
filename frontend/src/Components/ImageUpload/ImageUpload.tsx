import React, { useState, useRef } from 'react';
import axios from 'axios';

const ImageUpload: React.FC = () => {
    const [file, setFile] = useState<File | null>(null);
    const [filePath, setFilePath] = useState<string | null>(null);
    const fileInputRef = useRef<HTMLInputElement | null>(null);

    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.files && event.target.files[0]) {
            const selectedFile = event.target.files[0];
            setFile(selectedFile);
            setFilePath(URL.createObjectURL(selectedFile));
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
        }
    };

    const handleRemoveImage = () => {
        setFile(null);
        setFilePath(null);
        if (fileInputRef.current) {
            fileInputRef.current.value = '';
        }
    };

    const handleOCR = async () => {
        try {
            const file = filePath;
            if (!file) {
                console.error("No auth token found in localStorage.");
                return;
            }

            const response = await axios.post("/api/upload", {
                file: {
                    path: `${file}`
                }
            });
            console.log("OCR resource:", response.data);
        } catch (error) {
            console.error("OCR error:", error);
        }
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
                        Drag & Drop your image here
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
                    <div className="relative">
                        <img src={filePath} alt="Selected" className="max-w-xs sm:max-w-md md:max-w-lg lg:max-w-screen-md" />
                        <button
                            onClick={handleRemoveImage}
                            className="absolute top-2 right-2 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center"
                        >
                            &times;
                        </button>
                    </div>
                    <div className="mt-4 w-full text-center">
                        <p className="mt-2">File Path: {filePath}</p>
                        <div className="mt-4 flex flex-col space-y-2">
                            <button
                                onClick={handleOCR}
                                className="bg-yellow-500 text-white py-2 px-4 rounded hover:bg-yellow-600"
                            >
                                OCR하기
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ImageUpload;
