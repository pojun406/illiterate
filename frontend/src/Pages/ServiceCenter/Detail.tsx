import axios from 'axios';
import React, { useEffect } from 'react';
import { useParams } from 'react-router-dom';

function Detail() {
    const { boardIdx } = useParams();

    useEffect(() => {
        console.log(boardIdx);

        fetchPost();
    }, [boardIdx]);

    const fetchPost = async () => {
        const response = await axios.post(`/board/post/detail`,
            {
                BoardIdx: boardIdx
            },
            {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`
                }
            }
        );
        console.log(response.data);
    };

    return (
        <div className="p-6 max-w-[1260px] mx-auto">
            <h1>Detail</h1>
        </div>
    );
}

export default Detail;
