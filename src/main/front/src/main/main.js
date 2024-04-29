import React, { useEffect, useState } from "react";
import axios from "axios";

function Main() {
    const [hello, setHello] = useState('');

    useEffect(() => {
        axios.get('/api/test')
            .then((res) => {
                setHello(res.data)
            })
    }, []); // 의존성 배열 추가

    return (
        <div className="main">
            <div>
                백엔드 데이터 : {hello}
            </div>
        </div>
    );
}

export default Main;
