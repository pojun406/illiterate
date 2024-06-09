import React, { useEffect, useState } from 'react';

interface DocumentAProps {
    data: any;
    filePath: string;
}

const DocumentA: React.FC<DocumentAProps> = ({ data, filePath }) => {
    const [ocrResult, setOcrResult] = useState<any>(null);

    useEffect(() => {
        // Mock OCR result for demonstration
        const mockOcrResult = {
            name: "강욱자",
            registrationNumber: "880104 2278105",
            contact: "043 641 4743",
            previousAddress: "경상남도 양산군",
            currentAddress: "제주특별자치도 수영영동구 서초3동 153번지 2",
            householdMembers: [
                { name: "김자현", contact: "017 318 9829" }
            ],
            moveReason: "직업 (취업, 사업, 직장 이전 등)"
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
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="registrationNumber">주민등록번호</label>
                            <input type="text" id="registrationNumber" value={ocrResult.registrationNumber} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" readOnly />
                        </div>
                        <div>
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="contact">연락처</label>
                            <input type="text" id="contact" value={ocrResult.contact} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" readOnly />
                        </div>
                        <div>
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="previousAddress">전에 살던 곳</label>
                            <input type="text" id="previousAddress" value={ocrResult.previousAddress} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" readOnly />
                        </div>
                        <div>
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="currentAddress">현재 사는 곳</label>
                            <input type="text" id="currentAddress" value={ocrResult.currentAddress} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" readOnly />
                        </div>
                        <div>
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="householdMemberName">세대주 성명</label>
                            <input type="text" id="householdMemberName" value={ocrResult.householdMembers[0].name} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" readOnly />
                        </div>
                        <div>
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="householdMemberContact">연락처</label>
                            <input type="text" id="householdMemberContact" value={ocrResult.householdMembers[0].contact} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" readOnly />
                        </div>
                        <div>
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="moveReason">전입 사유</label>
                            <input type="text" id="moveReason" value={ocrResult.moveReason} className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" readOnly />
                        </div>
                        <button type="button" className="w-full px-4 py-2 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none">저장하기</button>
                    </form>
                )}
            </div>
        </div>
    );
};

export default DocumentA;