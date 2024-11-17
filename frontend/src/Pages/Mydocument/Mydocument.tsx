import { useState } from "react";
import fetchWithAuthGet from "../../Components/AccessToken/AccessTokenGet";
const Mydocument = () => {
    const [documents, setDocuments] = useState<any[]>([]);
    fetchWithAuthGet("/ocr/posts", null)
        .then((res) => {
            console.log(res);
        })
        .catch((error) => {
            console.error("Error fetching documents:", error);
        });

    
    return (
        <div className="flex p-6 w-[1260px] justify-center items-center mx-auto">
            <p>내 문서함</p>
        </div>
    );
};

export default Mydocument;
