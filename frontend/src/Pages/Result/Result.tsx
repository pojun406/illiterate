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
    const [filePath, setFilePath] = useState<string | undefined>(() => {
        return location.state?.filePath || localStorage.getItem('filePath') || undefined;
    });
    const [sidebarOpen, setSidebarOpen] = useState(false);
    const [isSaved, setIsSaved] = useState(false);
    const [file, setFile] = useState<File | null>(location.state?.file || null);

    useEffect(() => {
        if (!location.state?.fromImageUpload) {
            navigate('/application');
        }

        const handleBeforeUnload = (event: BeforeUnloadEvent) => {
            if (!isSaved) {
                event.preventDefault();
                event.returnValue = '';
            }
        };

        window.addEventListener('beforeunload', handleBeforeUnload);

        return () => {
            window.removeEventListener('beforeunload', handleBeforeUnload);
        };
    }, [location, navigate, data, isSaved]);

    const handleDataLoaded = (loadedData: any) => {
        setData(loadedData);
    };

    const handleSave = () => {
        setIsSaved(true);
    };

    const renderDocument = () => {
        console.log(data);
        if (data && data[0]) {
            if (data[0].title === '출생신고서') {
                console.log(data[0].title + " 출생신고서");
                return <DocumentA data={data} filePath={filePath as string} onSave={handleSave} />;
            } else if (data[0].title === '전입신고서') {
                console.log(data[0].title + " 전입신고서");
                return <DocumentB data={data} filePath={filePath as string} onSave={handleSave} />;
            } else {
                return <div>문서 형식을 확인해 주세요</div>;
            }
        } else {
            return <div>문서 형식을 확인해 주세요</div>;
        }
    };
    
    let scale = 1;
    const zoomIn = () => {
        scale += 0.1;
        const documentImage = document.getElementById('documentImage');
        if (documentImage) {
            documentImage.style.transform = `scale(${scale})`;
            documentImage.style.transformOrigin = 'left top';
        }
    };
    const zoomOut = () => {
        scale = Math.max(0.1, scale - 0.1);
        const documentImage = document.getElementById('documentImage');
        if (documentImage) {
            documentImage.style.transform = `scale(${scale})`;
            documentImage.style.transformOrigin = 'left top';
        }
    };
    const resetZoom = () => {
        scale = 1;
        const documentImage = document.getElementById('documentImage');
        if (documentImage) {
            documentImage.style.transform = `scale(${scale})`;
            documentImage.style.transformOrigin = 'left top';
        }
    };

    return (
        <div className="flex p-6 bg-gray-100 select-none">
            <Sidebar sidebarOpen={sidebarOpen} setSidebarOpen={setSidebarOpen} />
            <div className="flex flex-col items-center justify-center w-full md:mx-4">
                {!data && <OCR onDataLoaded={handleDataLoaded} file={file} />}
                {data && (
                    <div className="flex flex-col lg:flex-row justify-center items-start space-y-4 lg:space-y-0 lg:space-x-4 w-full">
                        <div className="flex-1 p-3 bg-white items-center justify-center shadow-md rounded-lg relative w-full min-h-[610px] md:min-h-[620px] lg:min-h-[1044px] lg:h-[1044px]">
                            <div className="absolute top-2 left-2 flex space-x-2 z-10 opacity-75">
                                <button className="bg-blue-500 text-white px-2 py-1 rounded" onClick={() => zoomIn()}>확대</button>
                                <button className="bg-blue-500 text-white px-2 py-1 rounded" onClick={() => zoomOut()}>축소</button>
                                <button className="bg-blue-500 text-white px-2 py-1 rounded" onClick={() => resetZoom()}>원본이미지</button>
                            </div>
                            <div className="overflow-auto flex justify-center items-start h-full">
                                <img id="documentImage" src={filePath} alt="이미지" className="rounded-lg transform-origin-top-left object-contain relative w-full h-full"/>
                            </div>
                        </div>
                        <div className="flex-1 p-3 bg-white shadow-md rounded-lg mt-4 mx-auto w-full lg:w-auto h-full lg:h-[1044px] overflow-auto">
                            {renderDocument()}
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Result;