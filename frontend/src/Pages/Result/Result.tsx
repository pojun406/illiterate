import { useEffect, useState, useRef } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

interface BoundingBox {
  x1: number;
  y1: number;
  x2: number;
  y2: number;
}

interface BirthData {
  [key: string]: string;
}

const Result = () => {
    const [filePath1, setFilePath1] = useState<string | undefined>(undefined);
    const [filePath2, setFilePath2] = useState<string | undefined>("/mockup/birth.png");
    const location = useLocation();
    const navigate = useNavigate();
    const [boxes, setBoxes] = useState<{ [key: string]: BoundingBox }>({});
    const [labelMap, setLabelMap] = useState<{ [key: string]: string }>({});
    const [birthData, setBirthData] = useState<BirthData>({});
    const [originalKeys, setOriginalKeys] = useState<{ [key: string]: string }>({});
    const isFirstRender = useRef(true);

    const SCALE_FACTOR = {
        x: 620 / 2500,
        y: 877 / 3540
    };

    const VERTICAL_OFFSET = 30; // 세로 방향으로 30픽셀 아래로 이동

    useEffect(() => {
        if (location.state && location.state.filePath) {
            setFilePath1(location.state.filePath);
        }
    }, [location.state]);

    useEffect(() => {
        Promise.all([
            fetch('/mockup/birth.json').then(response => response.json()),
            fetch('/mockup/birthdata.json').then(response => response.json())
        ])
        .then(([birthJson, birthDataJson]) => {
            const parsedBoxes: { [key: string]: BoundingBox } = {};
            const initialLabelMap: { [key: string]: string } = {};
            const initialOriginalKeys: { [key: string]: string } = {};
            Object.entries(birthJson).forEach(([key, value]) => {
                const coordinates = key.match(/\d+(\.\d+)?/g);
                if (coordinates && coordinates.length === 8) {
                    const [x1, y1, x2, y2, x3, y3, x4, y4] = coordinates.map(Number);
                    const labelValue = value as string; // 타입 단언
                    parsedBoxes[labelValue] = {
                        x1: Math.round(Math.min(x1, x3) * SCALE_FACTOR.x),
                        y1: Math.round(Math.min(y1, y2) * SCALE_FACTOR.y),
                        x2: Math.round(Math.max(x2, x4) * SCALE_FACTOR.x),
                        y2: Math.round(Math.max(y3, y4) * SCALE_FACTOR.y)
                    };
                    initialLabelMap[labelValue] = labelValue;
                    initialOriginalKeys[labelValue] = labelValue;
                }
            });
            setBoxes(parsedBoxes);
            setLabelMap(initialLabelMap);
            setOriginalKeys(initialOriginalKeys);
            if (isFirstRender.current) {
                console.log(birthDataJson);
                isFirstRender.current = false;
            }
            setBirthData(birthDataJson as BirthData);
        })
        .catch(error => console.error('Error loading data:', error));
    }, [SCALE_FACTOR.x, SCALE_FACTOR.y]);

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

    const handleLabelChange = (oldLabel: string, newValue: string) => {
        const originalKey = originalKeys[oldLabel];
        setBirthData(prev => ({
            ...prev,
            [originalKey]: newValue
        }));
    };

    const handleInputBlur = (oldLabel: string, newLabel: string) => {
        const originalKey = originalKeys[oldLabel];
        
        setBoxes(prev => {
            const updated = { ...prev };
            if (oldLabel !== newLabel && updated[oldLabel]) {
                updated[newLabel] = updated[oldLabel];
                delete updated[oldLabel];
            }
            return updated;
        });

        setLabelMap(prev => {
            const updated = { ...prev };
            if (oldLabel !== newLabel) {
                updated[newLabel] = newLabel;
                delete updated[oldLabel];
            }
            return updated;
        });

        setOriginalKeys(prev => {
            const updated = { ...prev };
            if (oldLabel !== newLabel) {
                updated[newLabel] = originalKey;
                delete updated[oldLabel];
            }
            return updated;
        });

        setBirthData(prev => {
            const updated = { ...prev };
            if (oldLabel !== newLabel) {
                updated[originalKey] = updated[originalKey];
            }
            return updated;
        });
    };

    const handleRegister = async () => {
        try {
            // 콘솔 로그 제거 또는 개발 모드에서만 출력
            if (process.env.NODE_ENV === 'development') {
                console.log('BirthData:', birthData);
            }
            alert('데이터가 성공적으로 등록되었습니다.');
        } catch (error) {
            console.error('Error registering data:', error);
            alert('데이터 등록 중 오류가 발생했습니다.');
        }
    };

    // 토스트 메시지를 표시하는 함수 (별도로 구현 필요)
    const showToast = (message: string, type: 'success' | 'error' = 'success') => {
        // 토스트 메시지 표시 로직
    };

    return (
        <div className='flex flex-col w-[1260px] h-full mx-auto my-4'>
            <h1 className='text-4xl font-bold'>전자문서 등록</h1>
            {filePath1 && filePath2 ? (
                <div className='flex flex-row items-center justify-between mt-4'>
                    <div>
                        <h2 className='text-2xl font-bold'>BEFORE</h2>
                        <img src={filePath1 || ''} alt="원본 이미지" className='w-[620px] h-[877px] border-2 border-gray-300'/>
                    </div>
                    <div className='relative'>
                        <h2 className='text-2xl font-bold'>AFTER</h2>
                        <img src={filePath2 || ''} alt="처리된 이미지" className='w-[620px] h-[877px] border-2 border-gray-300'/>
                        {Object.entries(boxes).map(([label, box]) => {
                            if (!box) return null; // box가 undefined인 경우 렌더링하지 않음
                            const { left, top, width, height } = scaleBox(box);
                            const currentLabel = labelMap[label] !== undefined ? labelMap[label] : label;
                            const originalKey = originalKeys[currentLabel];
                            const value = birthData[originalKey] || "";
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
                <button className='bg-red-400 text-white px-4 py-2 rounded-md hover:bg-red-500' onClick={() => navigate('/application')}>돌아가기</button>
                <button className='bg-blue-400 text-white px-4 py-2 rounded-md hover:bg-blue-500' onClick={handleRegister}>등록하기</button>
            </div>
        </div>
    );
};

export default Result;
