import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import fetchWithAuthGet from "../../Components/AccessToken/AccessTokenGet";

interface Document {
    resultIdx: number;
    title: string;
    infoTitle: string;
    createdAt: string;
    modifyAt: string;
}

const Mydocument = () => {
    const [documents, setDocuments] = useState<Document[]>([]);
    const [currentPage, setCurrentPage] = useState(1);
    const documentsPerPage = 12;
    const navigate = useNavigate();
    const [noDocumentsMessage, setNoDocumentsMessage] = useState<string | null>(null);

    useEffect(() => {
        fetchWithAuthGet("/ocr/posts", null)
            .then((res) => {
                if (typeof res === 'string') {
                    return;
                }
                
                const errorCode = res?.data?.errorCode;
                const errorMessage = res?.data?.errorMessage;

                if (errorCode === 400 && errorMessage === "존재하지 않는 문서입니다.") {
                    setNoDocumentsMessage("등록한 문서가 없습니다.");
                    return;
                }

                if (Array.isArray(res?.data?.data)) {
                    setDocuments(res.data.data);
                    setNoDocumentsMessage(null);
                } else {
                    console.error("예상치 못한 응답 형식:", res);
                    setDocuments([]);
                }
            })
            .catch((error) => {
                console.error("문서 가져오기 오류:", error);
                alert("문서를 가져오는데 실패했습니다.");
            });
    }, []);

    const handleDocumentClick = (resultIdx: number) => {
        navigate(`/mydocument/detail/${resultIdx}`);
    };

    // 페이지네이션 계산
    const indexOfLastDocument = currentPage * documentsPerPage;
    const indexOfFirstDocument = indexOfLastDocument - documentsPerPage;
    const currentDocuments = documents.slice(indexOfFirstDocument, indexOfLastDocument);
    const totalPages = Math.ceil(documents.length / documentsPerPage);

    const handlePageChange = (pageNumber: number) => {
        setCurrentPage(pageNumber);
    };
    
    return (
        <div className="flex flex-col p-6 w-[1260px] mx-auto">
            <h1 className="text-2xl font-bold mb-6">내 문서함</h1>
            {noDocumentsMessage ? (
                <div className="flex justify-center items-center min-h-[750px]">
                    <p className="text-center text-red-500">{noDocumentsMessage}</p>
                </div>
            ) : (
                <div className="grid grid-cols-2 gap-4 mb-6 min-h-[607.5px]">
                    {currentDocuments.map((doc) => (
                        <div 
                            key={doc.resultIdx} 
                            className="border rounded-lg p-4 shadow-sm hover:shadow-md transition-shadow cursor-pointer min-h-[83.5px]"
                            onClick={() => handleDocumentClick(doc.resultIdx)}
                        >
                            <div className="flex justify-between items-center mb-2">
                                <h3 className="font-semibold">{doc.title}</h3>
                                <p className="text-sm text-gray-600">생성일: {doc.createdAt}</p>
                            </div>
                            <div className="flex justify-between items-center">
                                <p className="text-sm text-gray-600">문서 종류: {doc.infoTitle}</p>
                                <p className="text-sm text-gray-600">수정일: {doc.modifyAt}</p>
                            </div>
                        </div>
                    ))}
                    {/* 빈 공간을 채우기 위한 빈 div */}
                    {Array.from({ length: documentsPerPage - currentDocuments.length }).map((_, index) => (
                        <div key={`empty-${index}`} className="border rounded-lg p-4 invisible min-h-[83.5px]"></div>
                    ))}
                </div>
            )}
            {totalPages > 1 && (
                <div className="flex justify-center gap-2">
                    {Array.from({ length: totalPages }, (_, i) => i + 1).map((pageNum) => (
                        <button
                            key={pageNum}
                            onClick={() => handlePageChange(pageNum)}
                            className={`px-4 py-2 rounded ${
                                currentPage === pageNum
                                    ? 'bg-blue-500 text-white'
                                    : 'bg-gray-200 hover:bg-gray-300'
                            }`}
                        >
                            {pageNum}
                        </button>
                    ))}
                </div>
            )}
        </div>
    );
};

export default Mydocument;
