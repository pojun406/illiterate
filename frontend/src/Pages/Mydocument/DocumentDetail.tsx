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

    useEffect(() => {
        if (paramOcrId) {
            setOcrId(paramOcrId);
        }
    }, [paramOcrId]);

    useEffect(() => {
        if (ocrId) {
            console.log("Sending request with ocrId:", ocrId);
            fetchWithAuth(`/ocr/posts/${ocrId}`)
                .then((res) => {
                    if (typeof res === 'string') {
                        console.error("문서 가져오기 오류:", res);
                        return;
                    }
                    const data = res.data;
                    setDocumentData(data.data);
                    setOcrResult(JSON.parse(data.data.ocrResult));
                    console.log("요청한 문서:", data.data);
                })
                .catch((error) => {
                    console.error("문서 가져오기 오류:", error);
                });
        } else {
            console.log("ocrId is null or undefined");
        }
    }, [ocrId]);

    return (
        <div className='flex flex-col w-[1260px] h-full mx-auto my-4'>
            <h1 className='text-4xl font-bold'>{documentData?.title}</h1>
            {documentData?.originalImg ? (
                <div className='flex flex-row items-center justify-between mt-4'>
                    <div>
                        <h2 className='text-2xl font-bold'>BEFORE</h2>
                        <img src={`http://localhost:8080/images/${documentData?.originalImg.split('\\').pop()}`} alt="원본 이미지" className='w-[620px] h-[877px] border-2 border-gray-300'/>
                    </div>
                    <div className='relative'>
                        
                    </div>
                </div>
            ) : (
                <div>
                    <h2>등록된 이미지가 없습니다.</h2>
                </div>
            )}
            <div className='flex flex-row items-center justify-between mt-4'>
                <button className='bg-red-400 text-white px-4 py-2 rounded-md hover:bg-red-500' onClick={() => navigate('/mydocument')}>돌아가기</button>
            </div>
        </div>
    );
};

export default DocumentDetail;
