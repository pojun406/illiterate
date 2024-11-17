import { request } from 'http';
import fetchWithAuthGet from '../../Components/AccessToken/AccessToken';
import fetchWithAuth from '../../Components/AccessToken/AccessToken';
import { useEffect, useState, useRef } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';

interface BoundingBox {
  x1: number;
  y1: number;
  x2: number;
  y2: number;
}

interface BirthData {
  [key: string]: string;
}

interface OcrResult {
  document_index?: number;
  results: {
    label: string;
    text: string | { error: boolean };
    vector: string;
  }[];
}

interface LocationState {
  ocrId: string;
}

const DocumentUpdate = () => {
    const [filePath1, setFilePath1] = useState<string | undefined>(undefined);
    const navigate = useNavigate();
    const location = useLocation();
    const { ocrId: paramOcrId } = useParams<{ ocrId: string }>();
    const [boxes, setBoxes] = useState<{ [key: string]: BoundingBox }>({});
    const [labelMap, setLabelMap] = useState<{ [key: string]: string }>({});
    const [birthData, setBirthData] = useState<BirthData>({});
    const [originalKeys, setOriginalKeys] = useState<{ [key: string]: string }>({});
    const isFirstRender = useRef(true);
    const [decodedOcrResult, setDecodedOcrResult] = useState<OcrResult | null>(null);
    const [ocrId, setOcrId] = useState<string | null>(null);
    const [ocrResult, setOcrResult] = useState<any>(null);
    const [title, setTitle] = useState<string>("");

    const SCALE_FACTOR = {
        x: 620 / 2500,
        y: 877 / 3540
    };

    const VERTICAL_OFFSET = 30;

    useEffect(() => {
        if (paramOcrId) {
            setOcrId(paramOcrId);
        } else {
            console.error("ocrId가 null입니다. URL을 확인하세요.");
            // navigate('/error'); // 필요에 따라 오류 페이지로 이동
        }
    }, [paramOcrId]);

    useEffect(() => {
        if (ocrId) {
            console.log("Sending request with ocrId:", ocrId);
            fetchWithAuthGet(`/ocr/posts/${ocrId}`)
                .then((res) => {
                    console.log("res:", res);
                    if (typeof res === 'string') {
                        console.error("문서 가져오기 오류:", res);
                        return;
                    }
                    if (!res.data?.data?.ocrResult) {
                        console.error("문서 데이터 구조가 올바르지 않습니다");
                        return;
                    }
                    const data = res.data;
                    try {
                        const ocrResultString = data.data.ocrResult;
                        const ocrResult = JSON.parse(ocrResultString);
                        if (!Array.isArray(ocrResult.results)) {
                            console.error("OCR 결과에 results 배열이 없습니다.");
                            return;
                        }
                        console.log("요청한 문서:", ocrResult);
                        setOcrResult(ocrResult);
                    } catch (error) {
                        console.error("OCR 결과 파싱 오류:", error);
                    }

                    const originalImgPath = data.data.originalImg;
                    const cleanedImgPath = 'http://localhost:8080/image/ocr/' + originalImgPath.replace('C:\\app\\image\\ocr\\', '');
                    
                    console.log("Cleaned Image Path:", cleanedImgPath);
                    setFilePath1(cleanedImgPath);

                    setTitle(data.data.title);
                })
                .catch((error) => {
                    console.error("문서 가져오기 오류:", error);
                });
        }
    }, [ocrId]);

    useEffect(() => {
        if (ocrResult && Array.isArray(ocrResult.results)) {
            console.log('OCR 결과:', ocrResult);
            
            const parsedBoxes: { [key: string]: BoundingBox } = {};
            const initialLabelMap: { [key: string]: string } = {};
            const initialOriginalKeys: { [key: string]: string } = {};
            const initialBirthData: BirthData = {};

            ocrResult.results.forEach((result: any) => {
                const coordinates = result.vector.match(/\d+(\.\d+)?/g);
                if (coordinates && coordinates.length === 8) {
                    const [x1, y1, x2, y2, x3, y3, x4, y4] = coordinates.map(Number);
                    const labelValue = result.label;
                    parsedBoxes[labelValue] = {
                        x1: Math.round(Math.min(x1, x3) * SCALE_FACTOR.x),
                        y1: Math.round(Math.min(y1, y2) * SCALE_FACTOR.y),
                        x2: Math.round(Math.max(x2, x4) * SCALE_FACTOR.x),
                        y2: Math.round(Math.max(y3, y4) * SCALE_FACTOR.y)
                    };
                    initialLabelMap[labelValue] = labelValue;
                    initialOriginalKeys[labelValue] = labelValue;
                    initialBirthData[labelValue] = typeof result.text === 'string' ? result.text : "";
                }
            });

            setBoxes(parsedBoxes);
            setLabelMap(initialLabelMap);
            setOriginalKeys(initialOriginalKeys);
            setBirthData(initialBirthData);
            setDecodedOcrResult(ocrResult);
        }
    }, [ocrResult]);

    const scaleBox = (box: BoundingBox | undefined) => {
        if (!box) {
            return { left: 0, top: 0, width: 0, height: 0 };
        }
        return {
            left: box.x1,
            top: box.y1,
            width: box.x2 - box.x1,
            height: box.y2 - box.y1,
        };
    };

    const handleLabelChange = (label: string, newValue: string) => {
        const originalKey = originalKeys[label];
        setBirthData(prev => ({
            ...prev,
            [originalKey]: newValue
        }));

        setDecodedOcrResult(prev => {
            if (!prev) return prev;
            const updatedResults = prev.results.map((result: any) => {
                if (result.label === label) {
                    return { ...result, text: newValue };
                }
                return result;
            });
            console.log('Updated OCR Result:', updatedResults);
            return { ...prev, results: updatedResults };
        });
    };

    const handleInputBlur = (oldLabel: string, newLabel: string) => {
        if (oldLabel !== newLabel) {
            setBirthData(prev => {
                const updated = { ...prev };
                updated[oldLabel] = newLabel;
                return updated;
            });
        }
    };

    const handleRegister = async () => {
        console.log('Current title:', title);
        console.log('Current OCR Result:', decodedOcrResult);

        if (decodedOcrResult && decodedOcrResult.results) {
            const updatedOcrResults = decodedOcrResult.results.map(result => {
                const element = document.getElementById(result.label) as HTMLInputElement | null;
                if (element) {
                    console.log(`Element found for label ${result.label}:`, element.value);
                    return {
                        ...result,
                        text: element.value
                    };
                } else {
                    console.warn(`Element not found for label ${result.label}`);
                }
                return result;
            });

            setDecodedOcrResult(prev => {
                if (!prev) {
                    console.error('Previous OCR result is null or undefined');
                    return prev;
                }
                return {
                    ...prev,
                    results: updatedOcrResults
                };
            });

            const requestDto = {
                title: title,
                ocrData: JSON.stringify({
                    document_index: decodedOcrResult?.document_index || 1,
                    results: updatedOcrResults.map(result => ({
                        vector: result.vector,
                        label: result.label,
                        text: result.text
                    }))
                }),
                ocrId: ocrId || ''
            };

            console.log('Request DTO:', requestDto);

            try {
                fetchWithAuth(`/ocr/posts/${ocrId}`, requestDto)
                    .then((response) => {
                        console.log("response:", response);
                        console.log("requestDto:", requestDto);
                        if (typeof response !== 'string' && response.data.code === 200) {
                            alert('수정이 완료되었습니다.');
                            navigate('/mydocument');
                        } else {
                            console.error('수정 실패:', response);
                            alert('수정에 실패했습니다.');
                        }
                    })
                    .catch((error) => {
                        console.error('수정 중 오류 발생:', error);
                        alert('수정 중 오류가 발생했습니다.');
                    });
            } catch (error) {
                console.error('수정 중 오류 발생:', error);
                alert('수정 중 오류가 발생했습니다.');
            }
        } else {
            console.error('OCR 결과가 없습니다.');
        }
    };

    return (
        <div className='flex flex-col w-[1260px] h-full mx-auto my-4'>
            <h1 className='text-4xl font-bold'>전자문서 등록</h1>
            <input 
                type="text" 
                id="title" 
                className='w-full h-10 mt-4 px-2 border border-black rounded-md' 
                placeholder='제목을 입력하세요.' 
                value={title}
                onChange={(e) => setTitle(e.target.value)}
            />
            {filePath1 ? (
                <div className='flex flex-row items-center justify-between mt-4'>
                    <div>
                        <h2 className='text-2xl font-bold'>BEFORE</h2>
                        <img src={filePath1} alt="원본 이미지" className='w-[620px] h-[877px] border-2 border-gray-300'/>
                    </div>
                    <div className='relative'>
                        <h2 className='text-2xl font-bold'>AFTER</h2>
                        <img src={filePath1} alt="처리된 이미지" className='w-[620px] h-[877px] border-2 border-gray-300'/>
                        {Object.entries(boxes).map(([label, box]) => {
                            if (!box) return null;
                            const { left, top, width, height } = scaleBox(box);
                            const currentLabel = labelMap[label] !== undefined ? labelMap[label] : label;
                            const originalKey = originalKeys[currentLabel];
                            
                            const result = decodedOcrResult?.results.find((result: any) => result.label === label);
                            const textValue = typeof result?.text === 'string' ? result.text : "";

                            const value = birthData[originalKey] || textValue || ""; 

                            return (
                                <div
                                    key={label}
                                    className='absolute'
                                    style={{
                                        left: `${left}px`,
                                        top: `${top + VERTICAL_OFFSET}px`,
                                        width: `${width}px`,
                                        height: `${height}px`,
                                    }}
                                >
                                    <input
                                        type="text"
                                        id={label}
                                        value={value}
                                        onChange={(e) => handleLabelChange(currentLabel, e.target.value)}
                                        onBlur={(e) => handleInputBlur(currentLabel, e.target.value)}
                                        className='w-full h-full border border-red-500 bg-white text-xs text-center'
                                    />        
                                </div>
                            );
                        })}
                    </div>
                </div>
            ) : (
                <div>
                    <h2>등록된 이미지가 없습니다.</h2>
                </div>
            )}
            <div className='flex flex-row items-center justify-between mt-4'>
                <button className='bg-red-400 text-white px-4 py-2 rounded-md hover:bg-red-500' onClick={() => navigate('/mydocument')}>돌아가기</button>
                <button className='bg-blue-400 text-white px-4 py-2 rounded-md hover:bg-blue-500' onClick={handleRegister}>수정하기</button>
            </div>
        </div>
    );
};

export default DocumentUpdate;
