import React, { useEffect, useState } from 'react';

interface DocumentBProps {
    data: any;
    filePath: string;
    onSave: () => void; // Added this line
}

const DocumentB: React.FC<DocumentBProps> = ({ data, filePath, onSave }) => {
    const [ocrResult, setOcrResult] = useState<any>(null);

    useEffect(() => {
        if (data && data.length > 0) {
            setOcrResult(data[0].fields);
            console.log(data[0].fields); // Changed to data[0].fields
        }
    }, [data]);

    if (!data) {
        return <div>데이터가 없습니다.</div>;
    }

    return (
        <>
            <h2 className="text-xl font-bold mb-4 text-center">OCR 결과</h2>
            {ocrResult && (
                <form className="space-y-1.5 grid grid-cols-2 gap-4">
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="전입자성명">전입자 성명</label>
                        <input type="text" id="전입자성명" defaultValue={ocrResult.전입자성명} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="주민앞">주민 앞</label>
                        <input type="text" id="주민앞" defaultValue={ocrResult.주민앞} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="주민뒤">주민 뒤</label>
                        <input type="text" id="주민뒤" defaultValue={ocrResult.주민뒤} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="상단연락처">상단 연락처</label>
                        <input type="text" id="상단연락처" defaultValue={ocrResult.상단연락처.join(', ')} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="시도">시도</label>
                        <input type="text" id="시도" defaultValue={ocrResult.시도} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="시군구">시군구</label>
                        <input type="text" id="시군구" defaultValue={ocrResult.시군구} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="세대주성명">세대주 성명</label>
                        <input type="text" id="세대주성명" defaultValue={ocrResult.세대주성명} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="하단연락처">하단 연락처</label>
                        <input type="text" id="하단연락처" defaultValue={ocrResult.하단연락처.join(', ')} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="주소">주소</label>
                        <input type="text" id="주소" defaultValue={ocrResult.주소.join(', ')} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="다가구주택명칭">다가구 주택 명칭</label>
                        <input type="text" id="다가구주택명칭" defaultValue={ocrResult.다가구주택명칭.join(', ')} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="세대원성명">세대원 성명</label>
                        <input type="text" id="세대원성명" defaultValue={ocrResult.세대원성명.join(', ')} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="신청인번호">신청인 번호</label>
                        <input type="text" id="신청인번호" defaultValue={ocrResult.신청인번호.join(', ')} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="휴대전화">휴대 전화</label>
                        <input type="text" id="휴대전화" defaultValue={ocrResult.휴대전화.join(', ')} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="신청인성명">신청인 성명</label>
                        <input type="text" id="신청인성명" defaultValue={ocrResult.신청인성명} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-2">
                        <button type="button" className="w-full px-3 py-1.5 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none" onClick={onSave}>저장하기</button>
                    </div>
                </form>
            )}
        </>
    );
};

export default DocumentB;
