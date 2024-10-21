import React, { useState, useEffect } from 'react';

interface Post {
    boardIdx: number;
    title: string;
    createdAt: string;
    status: string;
    userId: string | null;
}

const ServiceCenter = () => {
    const [posts, setPosts] = useState<Post[]>([]);

    useEffect(() => {
        const fetchPosts = async () => {
            try {
                const response = await fetch('/board/posts', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${localStorage.getItem('authToken')}`
                    },
                });
                const data = await response.json();
                console.log(data);
                setPosts(data.data);
            } catch (error) {
                console.error('게시글을 불러오는 중 오류가 발생했습니다:', error);
            }
        };

        fetchPosts();
    }, []);

    return (
        <div className="p-6 max-w-[1260px] mx-auto">
            <h2 className="text-2xl font-bold mb-6">고객센터</h2>
            <div className="space-y-4">
                <div className="hidden md:grid grid-cols-7 font-semibold bg-gray-100 p-3 rounded-t-lg text-center">
                    <div>번호</div>
                    <div className='col-span-3'>제목</div>
                    <div>작성자</div>
                    <div>작성일</div>
                    <div>상태</div>
                </div>
                {posts.map((post) => (
                    <div key={post.boardIdx} className="hidden md:grid grid-cols-7 p-3 hover:bg-gray-50 transition-colors text-center">
                        <div className='flex justify-center'>{post.boardIdx}</div>
                        <div className='col-span-3'>{post.title}</div>
                        <div>{post.userId}</div>
                        <div>{post.createdAt}</div>
                        <div>{post.status}</div>
                    </div>
                ))}
                {posts.map((post) => (
                    <div key={post.boardIdx} className="md:hidden bg-white shadow-sm rounded-lg overflow-hidden p-4 text-center">
                        <div className="grid grid-cols-2 gap-2">
                            <div className="font-semibold">번호:</div>
                            <div className='flex justify-center'>{post.boardIdx}</div>
                            <div className="font-semibold">제목:</div>
                            <div className="font-medium">{post.title}</div>
                            <div className="font-semibold">작성자:</div>
                            <div>{post.userId}</div>
                            <div className="font-semibold">작성일:</div>
                            <div>{post.createdAt}</div>
                            <div className="font-semibold">상태:</div>
                            <div>{post.status}</div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default ServiceCenter;
