import React from 'react';

interface DocumentBProps {
    data: any;
}

const DocumentB: React.FC<DocumentBProps> = ({ data }) => {
    if (!data) {
        return <div>데이터가 없습니다.</div>;
    }

    return (
        <div className="relative w-full h-full flex justify-center items-center">
            <div className="p-4">
                <p className="text-lg">json: /mockup/ocrmockup.json </p>
                <p className="text-lg">제목: {data[0].title} </p>
                <p className="text-lg">이미지: <img src={data.filePath} alt="이미지" /> </p>
            </div>
        </div>
    );
};

export default DocumentB;