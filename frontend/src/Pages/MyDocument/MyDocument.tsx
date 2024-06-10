import React, { useEffect, useState } from 'react';
import DocumentA from "../../Components/DocumentType/DocumentA/DocumentA";
import DocumentB from "../../Components/DocumentType/DocumentB/DocumentB";
import Sidebar from "../../Components/Sidebar/Sidebar";

const MyDocument = () => {
    const [documents, setDocuments] = useState<any[]>([]);
    const [filePath, setFilePath] = useState<string | null>(null);
    const [sidebarOpen, setSidebarOpen] = useState(false);

    useEffect(() => {
        const savedDocuments = sessionStorage.getItem('savedDocuments');
        const savedFilePath = sessionStorage.getItem('filePath');
        if (savedDocuments) {
            setDocuments(JSON.parse(savedDocuments));
        }
        if (savedFilePath) {
            setFilePath(savedFilePath);
        }
    }, []);

    const renderDocument = (document: any) => {
        if (document.title === 'A 문서') {
            return <DocumentA data={document} filePath={filePath as string} />;
        } else if (document.title === 'B 문서') {
            return <DocumentB data={document} filePath={filePath as string} />;
        } else {
            return <div>문서 형식을 확인해 주세요</div>;
        }
    };

    return (
        <div className="flex p-6 bg-gray-100" style={{ userSelect: 'none' }}>
            <Sidebar sidebarOpen={sidebarOpen} setSidebarOpen={setSidebarOpen} />
            <div className={`flex-auto bg-white rounded-lg shadow-md md:mx-4 transition-all ${sidebarOpen ? 'ml-64' : 'ml-0'} lg:ml-2`}>
                {documents.length === 0 ? (
                    <div className="text-center text-2xl">저장된 문서가 없습니다.</div>
                ) : (
                    documents.map((document, index) => (
                        <div key={index} className="mb-6">
                            {renderDocument(document)}
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default MyDocument;