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

    const handleDelete = async () => {
        try {
            const response = await fetchWithAuth(`/board/posts/delete/${boardIdx}`);
            if (typeof response === 'string') {
                console.error('Error deleting post:', response);
                return;
            }
            if (response?.data?.code === 'SUCCESS') {
                alert('삭제되었습니다.');
                console.log('Post deleted successfully');
            } else {
                console.error('Error deleting post:', response?.data?.message || 'Unknown error');
            }
            // 추가적인 로직이 필요하다면 여기에 작성하세요.
        } catch (error) {
            console.error('Error deleting post:', error);
        }
    };

    const handleRegister = () => {
        console.log('문서 등록하기');
    };

    return (
        <div className="p-6 max-w-[1260px] mx-auto min-h-[736px]">
            <Link to="/servicecenter">
                <h1 className="text-2xl font-bold mb-2">목록으로</h1>
            </Link>
            <div className="flex justify-end">
                {localStorage.getItem('role') === 'ROLE_USER' ? (
                    <button className="bg-red-500 text-white px-4 py-2 rounded-md hover:bg-red-600" onClick={handleDelete}>삭제하기</button>
                ) : localStorage.getItem('role') === 'ROLE_ADMIN' ? (
                    <button className="bg-green-500 text-white px-4 py-2 rounded-md hover:bg-green-600" onClick={handleRegister}>문서 등록하기</button>
                ) : null}
            </div>
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
                                src={post.imagePath.replace("https://", "http://").replace("/app", "/resources")}
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
