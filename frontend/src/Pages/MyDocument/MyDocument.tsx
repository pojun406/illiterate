import React, { useEffect, useState, useRef } from 'react';
import axios from 'axios';
import DocumentA from "../../Components/DocumentType/DocumentA/DocumentA";
import DocumentB from "../../Components/DocumentType/DocumentB/DocumentB";
import Sidebar from "../../Components/Sidebar/Sidebar";

const MyDocument = () => {
    const [documents, setDocuments] = useState<any[]>([]);
    const [filePath, setFilePath] = useState<string | null>(null);
    const [sidebarOpen, setSidebarOpen] = useState(false);
    const fetchDocumentsRef = useRef(false);

    useEffect(() => {
        if (fetchDocumentsRef.current) return;
        fetchDocumentsRef.current = true;

        const fetchDocuments = async () => {
            try {
                const authToken = localStorage.getItem('authToken');
                const refreshToken = localStorage.getItem('refreshToken');
                const userId = localStorage.getItem('id');

                if (!authToken || !refreshToken) {
                    console.error('인증 토큰이 없습니다.');
                    fetchDocumentsRef.current = false;
                    return;
                }

                const fetchData = async (token: string) => {
                    return await axios.post('/ocr/posts', {}, {
                        headers: {
                            'Authorization': `Bearer ${token}`,
                            'Content-Type': 'application/json'
                        }
                    });
                };

                let response;
                try {
                    response = await fetchData(authToken);
                } catch (error: any) {
                    if (error.response && error.response.status === 401) {
                        try {
                            const refreshResponse = await axios.post('/refresh', { refreshToken, userId }, {
                                headers: {
                                    'Content-Type': 'application/json'
                                }
                            });
                            const newAccessToken = refreshResponse.data.data.accessToken;
                            const newRefreshToken = refreshResponse.data.data.refreshToken;
                            localStorage.setItem('authToken', newAccessToken);
                            localStorage.setItem('refreshToken', newRefreshToken);
                            response = await fetchData(newAccessToken);
                        } catch (refreshError) {
                            console.error('토큰 갱신 요청 중 오류:', refreshError);
                            fetchDocumentsRef.current = false;
                            return;
                        }
                    } else {
                        throw error;
                    }
                }

                if (Array.isArray(response.data.data) && response.data.data.length > 0) {
                    setDocuments(response.data.data);
                }
            } catch (error) {
                console.error('문서를 가져오는 중 오류가 발생했습니다:', error);
            } finally {
                fetchDocumentsRef.current = false;
            }
        };

        fetchDocuments();
    }, []);

    const handleSave = () => {
        // 저장 로직을 여기에 추가하세요
        console.log('저장되었습니다.');
    };

    const renderDocument = (document: any) => {
        if (document.title === 'A 문서') {
            return <DocumentA data={document} filePath={filePath as string} onSave={handleSave} />;
        } else if (document.title === 'B 문서') {
            return <DocumentB data={document} filePath={filePath as string} onSave={handleSave} />;
        } else {
            return <div>문서 형식을 확인해 주세요</div>;
        }
    };

    return (
        <div className="flex p-6 bg-gray-100" style={{ userSelect: 'none' }}>
            <Sidebar sidebarOpen={sidebarOpen} setSidebarOpen={setSidebarOpen} />
            <div className={`flex-auto bg-white rounded-lg shadow-md md:mx-4 transition-all ${sidebarOpen ? 'ml-64' : 'ml-0'} lg:ml-2`}>
                {documents.length === 0 ? (
                    <div className="flex items-center justify-center h-full text-center text-2xl">저장된 문서가 없습니다.</div>
                ) : (
                    documents.map((document, index) => (
                        <div key={index} className="mb-6">
                            <div className="cursor-pointer" onClick={() => alert(JSON.stringify(document))}>
                                {document.title}
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default MyDocument;