import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import OCR from "../../Components/OCR/OCR";
import DocumentA from "../../Components/DocumentType/DocumentA/DocumentA";
import DocumentB from "../../Components/DocumentType/DocumentB/DocumentB";

const Result = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const [data, setData] = useState<any>(() => {
        const savedData = sessionStorage.getItem('ocrData');
        return savedData ? JSON.parse(savedData) : null;
    });
    const [filePath, setFilePath] = useState<string | null>(() => {
        return location.state?.filePath || sessionStorage.getItem('filePath');
    });

    useEffect(() => {
        if (!location.state?.fromImageUpload) {
            navigate('/application');
        }
    }, [location, navigate]);

    const handleDataLoaded = (loadedData: any) => {
        setData(loadedData);
        sessionStorage.setItem('ocrData', JSON.stringify(loadedData));
        if (filePath) {
            sessionStorage.setItem('filePath', filePath); // 파일 경로를 세션 스토리지에 저장
        }
    };


    return (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
            {!data && <OCR onDataLoaded={handleDataLoaded} />}
            {data ? (
                data[0].title === 'B 문서' ? <DocumentB data={data} /> : <DocumentA data={data} />
            ) : (
                <div>데이터를 불러오는 중입니다...</div>
            )}
        </div>
    );
};

export default Result;