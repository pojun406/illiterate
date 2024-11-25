import fetchWithAuth from '../../Components/AccessToken/AccessToken';
import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

interface DocumentData {
    createTime: string;
    emptyImg: string;
    infoTitle: string;
    modifyTime: string;
    ocrId: number;
    ocrResult: string;
    originalImg: string;
    title: string;
}

const DocumentDetail = () => {
    const [filePath1, setFilePath1] = useState<string | undefined>(undefined);
    const navigate = useNavigate();
    const { ocrId: paramOcrId } = useParams();
    const [ocrId, setOcrId] = useState<string | null>(null);
    const [ocrResult, setOcrResult] = useState<any>(null);
    const [documentData, setDocumentData] = useState<DocumentData | null>(null);
    const [filename, setFilename] = useState<string | null>(null);

    useEffect(() => {
        const ocrId = paramOcrId;
        if (ocrId) {
            fetchWithAuth(`/ocr/posts/${ocrId}`)
                .then((res) => {
                    if (typeof res === 'string') {
                        console.error("문서 가져오기 오류:", res);
                        return;
                    }
                    console.log(res);
                    const data = res.data;
                    setDocumentData(data.data);
                    setOcrResult(JSON.parse(data.data.ocrResult));
                    setFilename(data.data.originalImg);
                    console.log("filename : " + filename);
                })
                .catch((error) => {
                    console.error("문서 가져오기 오류:", error);
                });
        } else {    
            console.log("ocrId is null or undefined");
        }   

    }, [paramOcrId]);

    const documentdelete = () => {
        if (window.confirm('문서를 삭제하시겠습니까?')) {
            fetchWithAuth(`/ocr/posts/delete/${ocrId}`, { method: 'POST' })
                .then((res) => {
                    alert('문서가 성공적으로 삭제되었습니다.');
                    navigate('/mydocument');
                });
        }
    };

    return (
        <div className='flex flex-col w-[1260px] h-full mx-auto my-4 min-h-[703.5px]'>
            <div className='flex justify-between items-center mt-4'>
                <h1 className='text-4xl font-bold'>{documentData?.title}</h1>
                <button className='bg-red-500 text-white px-4 py-2 rounded-md hover:bg-red-600' onClick={documentdelete}>삭제</button>
            </div>
            {documentData?.originalImg ? (
                <div className='flex flex-row items-center justify-between mt-4'>
                    <div>
                        <h2 className='text-2xl font-bold'>원본 이미지</h2>
                        <img src={documentData?.originalImg.replace("/app", "")} alt="원본 이미지" className='w-[620px] h-[877px] border-2 border-gray-300'/>
                    </div>
                    <div className='relative'>
                        <h2 className='text-2xl font-bold'>저장된 데이터</h2>
                        <div className='w-[620px] h-[877px] border-2 border-gray-300 p-4 overflow-y-auto'>
                            <div className='space-y-4'>
                                {ocrResult?.results?.map((item: any, index: number) => (
                                    <div key={index} className="p-4 border rounded bg-white shadow-sm">
                                        <div className="flex items-center mb-2">
                                            <div className="bg-blue-100 text-blue-800 font-bold px-3 py-1 rounded-lg text-sm">
                                                {item.label}
                                            </div>
                                        </div>
                                        <div className="text-gray-700 bg-gray-50 p-3 rounded-lg">
                                            {item.text}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>
                </div>
            ) : (
                <div>
                    <h2>등록된 이미지가 없습니다.</h2>
                </div>
            )}
            <div className='flex flex-row items-center justify-between mt-4'>
                <button className='bg-red-400 text-white px-4 py-2 rounded-md hover:bg-red-500' onClick={() => navigate('/mydocument')}>돌아가기</button>
                <button className='bg-blue-400 text-white px-4 py-2 rounded-md hover:bg-blue-500' onClick={() => navigate(`/mydocument/edit/${ocrId}`)}>수정하기</button>
            </div>
        </div>
    );
};

export default DocumentDetail;
