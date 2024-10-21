import React, { useState, useEffect } from 'react';
import axios from 'axios';

interface Post {
    id: number;
    title: string;
    content: string;
    imagePath: string;
    status: string;
    userId: string | null;
}

const ServiceCenter = () => {
    const [posts, setPosts] = useState<Post[]>([]);

    useEffect(() => {
        const fetchPosts = async () => {
            try {
                const response = await fetch('/board/public/posts', {
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
            <h2 className="text-2xl font-bold mb-6">서비스 센터</h2>
            <div className="space-y-4">
                <div className="hidden md:grid grid-cols-4 font-semibold bg-gray-100 p-3 rounded-t-lg">
                    <div>번호</div>
                    <div>제목</div>
                    <div>내용</div>
                    <div>상태</div>
                </div>
                {posts.map((post) => (
                    <div key={post.id} className="bg-white shadow-sm rounded-lg overflow-hidden">
                        <div className="grid grid-cols-1 md:grid-cols-4 gap-2 p-4 hover:bg-gray-50 transition-colors">
                            {[
                                { label: '번호', value: post.id },
                                { label: '제목', value: post.title },
                                { label: '내용', value: post.content },
                                { label: '상태', value: post.status },
                            ].map(({ label, value }, index) => (
                                <React.Fragment key={`${post.id}-${label}`}>
                                    <div className="md:hidden font-semibold">{label}:</div>
                                    <div className={index === 1 ? "font-medium" : ""}>{value}</div>
                                </React.Fragment>
                            ))}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default ServiceCenter;
