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

    const handleUpload = async () => {
        if (file) {
            const formData = new FormData();
            formData.append('file', file);

            try {
                const response = await axios.post('http://localhost:8080/api/images/upload', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                });
                if (response.status === 200) {
                    alert('File uploaded successfully: ' + file.name);
                }
            } catch (error) {
                console.error('Error uploading the file:', error);
            }
        }
    };

    const handleRemoveImage = () => {
        setFile(null);
        setFilePath(null);
        if (fileInputRef.current) {
            fileInputRef.current.value = '';
        }
    };

    return (
        <div>
            {!filePath ? (
                <>
                    <div
                        onDragOver={handleDragOver}
                        onDrop={handleDrop}
                        style={{ border: '2px dashed #cccccc', padding: '20px', textAlign: 'center', cursor: 'pointer' }}
                    >
                        Drag & Drop your image here
                    </div>
                    <input
                        type="file"
                        ref={fileInputRef}
                        onChange={handleFileChange}
                        style={{ display: 'none' }}
                    />
                    <button onClick={() => fileInputRef.current?.click()}>Browse</button>
                </>
            ) : (
                <div style={{ position: 'relative', display: 'inline-block' }}>
                    <img src={filePath} alt="Selected" style={{ maxWidth: '100%' }} />
                    <button
                        onClick={handleRemoveImage}
                        style={{
                            position: 'absolute',
                            top: '10px',
                            right: '10px',
                            background: 'red',
                            color: 'white',
                            border: 'none',
                            borderRadius: '50%',
                            width: '25px',
                            height: '25px',
                            cursor: 'pointer',
                            textAlign: 'center',
                            lineHeight: '25px'
                        }}
                    >
                        &times;
                    </button>
                    <p>File Path: {filePath}</p>
                </div>
            )}
            {filePath && (
                <>
                    <hr/>
                    <button onClick={handleUpload}>Upload</button>
                </>
            )}
        </div>
    );
};

export default ImageUpload;
