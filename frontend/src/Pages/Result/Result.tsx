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
        const savedData = localStorage.getItem('ocrData');
        return savedData ? JSON.parse(savedData) : null;
    });
    const [filePath, setFilePath] = useState<string | null>(() => {
        return location.state?.filePath || localStorage.getItem('filePath');
    });
    const [sidebarOpen, setSidebarOpen] = useState(false);

    useEffect(() => {
        console.log('Loaded Data:', data[0].title); // 데이터 확인용 로그
        if (!location.state?.fromImageUpload) {
            navigate('/application');
        }
    }, [location, navigate, data]);

    const handleDataLoaded = (loadedData: any) => {
        console.log('Data Loaded:', loadedData); // 데이터 확인용 로그
        setData(loadedData);
        localStorage.setItem('ocrData', JSON.stringify(loadedData));
        if (filePath) {
            localStorage.setItem('filePath', filePath); // 파일 경로를 세션 스토리지에 저장
        }
    };

    const renderDocument = () => {
            console.log(data);
        if (data && data[0]) {
            if (data[0].title === 'B 문서') {
                console.log(data[0].title+"B 문서");
                return <DocumentB data={data} filePath={filePath as string} />;
            } else if (data[0].title === 'A 문서') {
                console.log(data[0].title+"A 문서")
                return <DocumentA data={data} filePath={filePath as string} />;
            } else {
                return <div>문서 형식을 확인해 주세요</div>;
            }
        } else {
            return <div>문서 형식을 확인해 주세요</div>;
        }
    };

    return (
        <div className="flex p-6 bg-gray-100" style={{ userSelect: 'none' }}>
            <Sidebar sidebarOpen={sidebarOpen} setSidebarOpen={setSidebarOpen} />
            <div className={`flex-auto bg-white rounded-lg shadow-md md:mx-4 transition-all ${sidebarOpen ? 'ml-64' : 'ml-0'} lg:ml-2`}>
                {!data && <OCR onDataLoaded={handleDataLoaded} />}
                {data && renderDocument()}
            </div>
        </div>
    );
};

export default Result;