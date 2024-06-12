import React, { useEffect, useState } from 'react';

interface DocumentAProps {
    data: any;
    filePath: string;
    onSave: () => void; // Added this line
}

const DocumentA: React.FC<DocumentAProps> = ({ data, filePath, onSave }) => {
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
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="한글성명">한글 성명</label>
                        <input type="text" id="한글성명" defaultValue={ocrResult.한글성명} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="한자성명">한자 성명</label>
                        <input type="text" id="한��성명" defaultValue={ocrResult.한자성명} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="본한자">본 한자</label>
                        <input type="text" id="본한자" defaultValue={ocrResult.본한자} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="출생일시">출생 일시</label>
                        <input type="text" id="출생일시" defaultValue={ocrResult.출생일시} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="등록기준지">등록 기준지</label>
                        <input type="text" id="등록기준지" defaultValue={ocrResult.등록기준지} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="주소">주소</label>
                        <input type="text" id="주소" defaultValue={ocrResult.주소} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="세대주및관계">세대주 및 관계</label>
                        <input type="text" id="세대주및관계" defaultValue={ocrResult.세대주및관계} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="외국국적">외국 국적</label>
                        <input type="text" id="외국국적" defaultValue={ocrResult.외국국적} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="부성명">부 성명</label>
                        <input type="text" id="부성명" defaultValue={ocrResult.부성명} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="모성명">모 성명</label>
                        <input type="text" id="모성명" defaultValue={ocrResult.모성명} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="부주민">부 주민등록번호</label>
                        <input type="text" id="부주민" defaultValue={ocrResult.부주민} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="모주민">모 주민등록번호</label>
                        <input type="text" id="모주민" defaultValue={ocrResult.모주민} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="부등록기준지">부 등록 기준지</label>
                        <input type="text" id="부등록기준지" defaultValue={ocrResult.부등록기준지} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="모등록기준지">모 등록 기준지</label>
                        <input type="text" id="모등록기준지" defaultValue={ocrResult.모등록기준지} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="신고인성명">신고인 성명</label>
                        <input type="text" id="신고인성명" defaultValue={ocrResult.신고인성명} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="신고인주민">신고인 주민등록번호</label>
                        <input type="text" id="신고인주민" defaultValue={ocrResult.신고인주민} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="기타자격">기타 자격</label>
                        <input type="text" id="기타자격" defaultValue={ocrResult.기타자격} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="신고인주소">신고인 주소</label>
                        <input type="text" id="신고인주소" defaultValue={ocrResult.신고인주소} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="신고인전화">신고인 전화</label>
                        <input type="text" id="신고인전화" defaultValue={ocrResult.신고인전화} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="신고인메일">신고인 메일</label>
                        <input type="text" id="신고인메일" defaultValue={ocrResult.신고인메일} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="임신주수">임신 주수</label>
                        <input type="text" id="임신주수" defaultValue={ocrResult.임신주수} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="신생아체중">신생아 체중</label>
                        <input type="text" id="신생아체중" defaultValue={ocrResult.신생아체중} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="실결혼시작일">실 결혼 시작일</label>
                        <input type="text" id="실결혼시작일" defaultValue={ocrResult.실결혼시작일} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-1">
                        <label className="block text-gray-700 text-sm font-bold mb-1" htmlFor="출산수">출산 수</label>
                        <input type="text" id="출산수" defaultValue={ocrResult.출산수} className="shadow appearance-none border rounded w-full py-1.5 px-2 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" />
                    </div>
                    <div className="col-span-2">
                        <button type="button" className="w-full px-3 py-1.5 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none" onClick={onSave}>저장하기</button>
                    </div>
                </form>
            )}
        </>
    );
};

export default DocumentA;
