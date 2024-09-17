import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const Result = () => {
    const [filePath1, setFilePath1] = useState<string | null>(null);
    const [filePath2, setFilePath2] = useState<string | null>("/mockup/dataX.png");
    const location = useLocation();
    const navigate = useNavigate();

    useEffect(() => {
        if (location.state && location.state.filePath) {
            setFilePath1(location.state.filePath);
        }
    }, [location]);

    return (
        <div className='flex flex-col w-[1260px] h-full mx-auto my-4'>
            <h1 className='text-4xl font-bold'>전자문서 등록</h1>
            {filePath1 && filePath2 ? (
                <div className='flex flex-row items-center justify-between mt-4'>
                    <div>
                        <h2 className='text-2xl font-bold'>BEFORE</h2>
                        <img src={filePath1} alt="원본 이미지" className='w-[620px] h-[877px] border-2 border-gray-300'/>
                    </div>
                    <div>
                        <h2 className='text-2xl font-bold'>AFTER</h2>
                        <img src={filePath2} alt="처리된 이미지" className='w-[620px] h-[877px] border-2 border-gray-300'/>
                    </div>
                </div>
            ) : (
                <div>
                    <h2>등록된 이미지가 없습니다.</h2>
                </div>
            )}
            <div className='flex flex-row items-center justify-between mt-4'>
                <button className='bg-red-400 text-white px-4 py-2 rounded-md hover:bg-red-500' onClick={() => navigate('/application')}>다시 등록</button>
                <button className='bg-blue-400 text-white px-4 py-2 rounded-md hover:bg-blue-500' onClick={() => navigate('/')}>등록하기</button>
            </div>
        </div>
    );
};

export default Result;
