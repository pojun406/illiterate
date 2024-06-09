import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import OCR from "../../Components/OCR/OCR";
import DocumentA from "../../Components/DocumentType/DocumentA/DocumentA";
import DocumentB from "../../Components/DocumentType/DocumentB/DocumentB";
import Sidebar from "../../Components/Sidebar/Sidebar";

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
    const [sidebarOpen, setSidebarOpen] = useState(false);

    useEffect(() => {
        console.log('Loaded Data:', data); // 데이터 확인용 로그
        if (!location.state?.fromImageUpload) {
            navigate('/application');
        }
    }, [location, navigate, data]);

    const handleDataLoaded = (loadedData: any) => {
        console.log('Data Loaded:', loadedData); // 데이터 확인용 로그
        setData(loadedData);
        sessionStorage.setItem('ocrData', JSON.stringify(loadedData));
        if (filePath) {
            sessionStorage.setItem('filePath', filePath); // 파일 경로를 세션 스토리지에 저장
        }
    };

    const renderDocument = () => {
        if (data && data[0] && data[0].title) {
            if (data[0].title === 'B 문서') {
                return <DocumentB data={data} filePath={filePath as string} />;
            } else if (data[0].title === 'A 문서') {
                return <DocumentA data={data} filePath={filePath as string} />;
            } else {
                return <div>문서 형식을 확인해 주세요</div>;
            }
        } else {
            return <div>문서 형식을 확인해 주세요</div>;
        }
    };

    return (
        <div className="flex">
            <Sidebar sidebarOpen={sidebarOpen} setSidebarOpen={setSidebarOpen} />
            <div className="flex-1 p-8">
                {!data && <OCR onDataLoaded={handleDataLoaded} />}
                {data && renderDocument()}
            </div>
        </div>
    );
};

export default Result;