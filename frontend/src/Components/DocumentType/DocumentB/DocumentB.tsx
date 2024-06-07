import React, { useEffect, useState } from 'react';

interface DocumentBProps {
    data: any;
    filePath: string;
}

const DocumentB: React.FC<DocumentBProps> = ({ data, filePath }) => {
    const [ocrResult, setOcrResult] = useState<any>(null);

    useEffect(() => {
        // Mock OCR result for demonstration
        const mockOcrResult = {
            name: "양현석",
            birthDate: "2001년 11월 28일",
            birthPlace: "강원도 시군구 읍면동",
            parentNames: [
                { name: "양삼순", registrationNumber: "090311-311104" },
                { name: "홍길동", registrationNumber: "060510-490126" }
            ],
            address: "제주특별자치도 수영영동구 선릉중길 04",
            contact: "016 725 7293",
            email: "sunja44@naver.com"
        };
        setOcrResult(mockOcrResult);
    }, []);

    if (!data) {
        return <div>데이터가 없습니다.</div>;
    }

    return (
        <div className="relative w-full h-full flex flex-col md:flex-row bg-gray-100 p-6">
            <div className="w-full md:w-1/2 p-4 flex items-center justify-center bg-white shadow-md rounded-lg">
                <img src={filePath} alt="이미지" className="max-w-full max-h-full rounded-lg" />
            </div>
            <div className="w-full md:w-1/2 p-4 bg-white shadow-md rounded-lg mt-6 md:mt-0 md:ml-6">
                <h2 className="text-2xl font-bold mb-6 text-center">OCR 결과</h2>
                {ocrResult && (
                    <form className="space-y-4">
                        <div>
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="name">성명</label>
                            <input type="text" id="name" value={ocrResult.name} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" readOnly />
                        </div>
                        <div>
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="birthDate">출생일</label>
                            <input type="text" id="birthDate" value={ocrResult.birthDate} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" readOnly />
                        </div>
                        <div>
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="birthPlace">출생지</label>
                            <input type="text" id="birthPlace" value={ocrResult.birthPlace} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" readOnly />
                        </div>
                        <div>
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="parentNames">부모 성명 및 주민등록번호</label>
                            <div className="space-y-2">
                                {ocrResult.parentNames.map((parent: any, index: number) => (
                                    <div key={index} className="flex space-x-2">
                                        <input type="text" value={parent.name} className="shadow appearance-none border rounded w-1/2 py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" readOnly />
                                        <input type="text" value={parent.registrationNumber} className="shadow appearance-none border rounded w-1/2 py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" readOnly />
                                    </div>
                                ))}
                            </div>
                        </div>
                        <div>
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="address">주소</label>
                            <input type="text" id="address" value={ocrResult.address} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" readOnly />
                        </div>
                        <div>
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="contact">연락처</label>
                            <input type="text" id="contact" value={ocrResult.contact} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" readOnly />
                        </div>
                        <div>
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="email">이메일</label>
                            <input type="text" id="email" value={ocrResult.email} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" readOnly />
                        </div>
                        <button type="button" className="w-full px-4 py-2 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none">저장하기</button>
                    </form>
                )}
            </div>
        </div>
    );
};

export default DocumentB;