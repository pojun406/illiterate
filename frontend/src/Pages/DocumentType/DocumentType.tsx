import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const DocumentType = () => {
    const navigate = useNavigate();
    const [selectedImage, setSelectedImage] = useState<File | null>(null);
    const [infoTitle, setInfoTitle] = useState<string>("");

    // 이미지 선택 핸들러
    const handleImageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        if (file) {
            setSelectedImage(file);
        }
    };

    // 문서 제목 입력 핸들러
    const handleTitleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setInfoTitle(event.target.value);
    };

    // 업로드 버튼 클릭 핸들러
    const handleButtonClick = async () => {
        if (!selectedImage) {
            alert("이미지를 선택해주세요.");
            return;
        }
        if (!infoTitle) {
            alert("문서 제목을 입력해주세요.");
            return;
        }

        try {
            const formData = new FormData();
            formData.append("file", selectedImage); // 이미지 파일 추가
            formData.append(
                "request",
                JSON.stringify({
                    infoTitle, // 문서 제목 추가
                })
            );



            // 백엔드 API 요청
            const response = await axios.post("http://localhost:8080/admin/paperinfo", formData, {
                headers: {
                    "Authorization": `Bearer ${localStorage.getItem("authToken")}`, // 필요 시 인증 토큰 추가
                },
            });

            console.log("FormData 내용 확인:");
            Array.from(formData.entries()).forEach(([key, value]) => {
                console.log(`${key}:`, value);
            });

            console.log("응답 데이터:", response.data);
            alert("문서 타입이 성공적으로 등록되었습니다!");
            navigate("/success");
        } catch (error) {
            console.error("문서 타입 등록 중 오류 발생:", error);
            alert("문서 타입 등록에 실패했습니다.");
        }
    };

    return (
        <div className="flex flex-col items-center p-6 w-[1260px] mx-auto">
            <h1 className="text-xl mb-4">문서 타입 등록 페이지</h1>
            {/* 문서 제목 입력 */}
            <input
                type="text"
                placeholder="문서 제목을 입력하세요"
                value={infoTitle}
                onChange={handleTitleChange}
                className="mb-4 px-4 py-2 border rounded w-full max-w-sm"
            />
            {/* 이미지 선택 */}
            <input
                type="file"
                accept="image/*"
                onChange={handleImageChange}
                className="mb-4"
            />
            {/* 업로드 버튼 */}
            <button
                onClick={handleButtonClick}
                className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
            >
                문서 타입 등록
            </button>
        </div>
    );
};

export default DocumentType;
