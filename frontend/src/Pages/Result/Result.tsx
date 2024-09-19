import { useEffect, useState } from 'react';
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

    const SCALE_FACTOR = {
        x: 620 / 2480,
        y: 877 / 3508
    };

    const VERTICAL_OFFSET = 29; // 세로 방향으로 5픽셀 아래로 이동

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
            Object.entries(birthJson).forEach(([key, value]) => {
                const coordinates = key.match(/\d+(\.\d+)?/g);
                if (coordinates && coordinates.length === 8) {
                    const [x1, y1, x2, y2, x3, y3, x4, y4] = coordinates.map(Number);
                    console.log(x1, y1, x2, y2, x3, y3, x4, y4);
                    const labelValue = value as string; // 타입 단언
                    parsedBoxes[labelValue] = {
                        x1: Math.round(Math.min(x1, x3) * SCALE_FACTOR.x),
                        y1: Math.round(Math.min(y1, y2) * SCALE_FACTOR.y),
                        x2: Math.round(Math.max(x2, x4) * SCALE_FACTOR.x),
                        y2: Math.round(Math.max(y3, y4) * SCALE_FACTOR.y)
                    };
                    initialLabelMap[labelValue] = labelValue;
                }
            });
            setBoxes(parsedBoxes);
            setLabelMap(initialLabelMap);

            setBirthData(birthDataJson as BirthData);
        })
        .catch(error => console.error('Error loading data:', error));
    }, [SCALE_FACTOR.x, SCALE_FACTOR.y]);

    const scaleBox = (box: BoundingBox) => {
        return {
            left: box.x1,
            top: box.y1,
            width: box.x2 - box.x1,
            height: box.y2 - box.y1,
        };
    };

    const handleLabelChange = (oldLabel: string, newLabel: string) => {
        setLabelMap(prev => ({
            ...prev,
            [oldLabel]: newLabel
        }));
        setBirthData(prev => ({
            ...prev,
            [oldLabel]: newLabel
        }));
    };

    const handleInputBlur = (oldLabel: string, newLabel: string) => {
        setBoxes(prev => {
            const updated = { ...prev };
            if (newLabel === '') {
                // 입력값이 비어있으면 해당 박스를 삭제
                delete updated[oldLabel];
            } else if (oldLabel !== newLabel) {
                // 라벨이 변경되었으면 박스 키를 업데이트
                updated[newLabel] = updated[oldLabel];
                delete updated[oldLabel];
            }
            return updated;
        });

        setLabelMap(prev => {
            const updated = { ...prev };
            if (newLabel === '') {
                // 입력값이 비어있으면 해당 라벨을 삭제
                delete updated[oldLabel];
            } else if (oldLabel !== newLabel) {
                // 라벨이 변경되었으면 라벨맵 업데이트
                delete updated[oldLabel];
                updated[newLabel] = newLabel;
            }
            return updated;
        });
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
                            const { left, top, width, height } = scaleBox(box);
                            const currentLabel = labelMap[label] !== undefined ? labelMap[label] : label;
                            const value = birthData[currentLabel];
                            return (
                                <div
                                    key={label}
                                    className='absolute'
                                    style={{
                                        left: `${left}px`,
                                        top: `${top + VERTICAL_OFFSET}px`, // 여기를 수정
                                        width: `${width}px`,
                                        height: `${height}px`,
                                    }}
                                >
                                    <input
                                        type="text"
                                        value={value !== undefined && value !== "" ? value : ""}
                                        onChange={(e) => handleLabelChange(label, e.target.value)}
                                        onBlur={(e) => handleInputBlur(label, e.target.value)}
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
                <button className='bg-red-400 text-white px-4 py-2 rounded-md hover:bg-red-500' onClick={() => navigate('/application')}>다시 등록</button>
                <button className='bg-blue-400 text-white px-4 py-2 rounded-md hover:bg-blue-500' onClick={() => navigate('/')}>등록하기</button>
            </div>
        </div>
    );
};

export default Result;
