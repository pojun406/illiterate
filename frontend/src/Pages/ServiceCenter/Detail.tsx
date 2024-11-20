import axios from 'axios';
import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import fetchWithAuth from '../../Components/AccessToken/AccessToken';

interface Post {
    boardIdx: number;
    title: string;
    content: string;
    userId: string;
    status: string;
    createdAt: string | null;
    imagePath: string;
}

function Detail() {
    const { boardIdx } = useParams();
    const [post, setPost] = useState<Post | null>(null);
    const [imagePath, setImagePath] = useState<string | null>(null);

    useEffect(() => {
        fetchPost();
    }, [boardIdx]);

    const fetchPost = async () => {
        try {
            const response = await fetchWithAuth(`/board/posts/${boardIdx}`, null);
            if (typeof response === 'string') {
                return;
            }
            if (response?.data?.data) {
                setPost(response.data.data);
                setImagePath(response.data.data.imagePath)

            }
        } catch (error) {
            console.error('Error fetching post:', error);
        }
    };

    return (
        <div className="p-6 max-w-[1260px] mx-auto">
            <Link to="/servicecenter">
                <h1 className="text-2xl font-bold mb-2">목록으로</h1>
            </Link>
            {post && (
                <div className="bg-white rounded-lg shadow-md p-6">
                    <div className="mb-6">
                        <h1 className="text-2xl font-bold mb-2">{post.title}</h1>
                        <div className="flex justify-between text-gray-600">
                            <span>작성자: {post.userId}</span>
                            <span>상태: {post.status}</span>
                        </div>
                    </div>
                    <div className="mb-6">
                        <p className="whitespace-pre-wrap">{post.content}</p>
                    </div>
                    {post.imagePath && (
                        <div className="mb-6">
                            <img 
                                src={"http://localhost:8080" + post.imagePath} 
                                alt="게시글 이미지" 
                                className="max-w-full h-auto rounded-lg"
                            />
                        </div>
                    )}
                </div>
            )}
        </div>
    );
}

export default Detail;
