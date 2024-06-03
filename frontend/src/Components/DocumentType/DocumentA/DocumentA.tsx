import React, { useEffect, useState } from 'react';

interface DocumentAProps {
    data: any;
}

const DocumentA: React.FC<DocumentAProps> = ({ data }) => {
    const [imageSrc, setImageSrc] = useState<string>('');

    useEffect(() => {
        const imageData = document.cookie.split('; ').find(row => row.startsWith('imageData='));
        if (imageData) {
            const base64Image = imageData.split('=')[1];
            setImageSrc(`data:image/jpeg;base64,${decodeURIComponent(base64Image)}`);
        }
    }, []);

    if (!data) {
        return <div>데이터가 없습니다.</div>;
    }

    return (
        <div className="relative w-full h-full flex justify-center items-center">
            <div className="p-4">
                <p className="text-lg">이미지: <img src={imageSrc || data.filePath} alt="이미지" /> </p>
                <p className="text-lg">json: /mockup/ocrmockup.json </p>
                <p className="text-lg">제목: {data[0].title} </p>
            </div>
        </div>
    );
};

export default DocumentA;