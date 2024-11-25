import fetchWithAuthGet from '../../Components/AccessToken/AccessTokenGet';
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

interface Post {
    boardIdx: number;
    title: string;
    createdAt: string;
    status: string;
    userId: string | null;
}

const ServiceCenter = () => {
    const [posts, setPosts] = useState<Post[]>([]);
    const [currentPage, setCurrentPage] = useState(1);
    const postsPerPage = 7;
    const navigate = useNavigate();

    useEffect(() => {
        const fetchPosts = async () => {
            try {
                const response = await fetchWithAuthGet('/board/posts', null);
                if (typeof response === 'string') {
                    setPosts([]);
                    return;
                }
                if (response?.data?.data) {
                    setPosts(response.data.data);
                } else {
                    setPosts([]);
                }
            } catch (error) {
                console.error('게시글을 불러오는 중 오류가 발생했습니다:', error);
                setPosts([]);
            }
        };

        fetchPosts();
    }, []);

    const handleItemClick = (boardIdx: number) => {
        navigate(`/servicecenter/detail/${boardIdx}`);
    };

    const handlePageChange = (pageNumber: number) => {
        setCurrentPage(pageNumber);
    };

    const indexOfLastPost = currentPage * postsPerPage;
    const indexOfFirstPost = indexOfLastPost - postsPerPage;
    const currentPosts = posts.slice(indexOfFirstPost, indexOfLastPost);

    const displayPosts = [...currentPosts, ...Array(postsPerPage - currentPosts.length).fill(null)];

    return (
        <div className="p-6 max-w-[1260px] mx-auto">
            <div className="flex justify-between items-center">
                <h2 className="text-2xl font-bold mb-6">고객센터</h2>
                <button className="bg-blue-500 text-white px-4 py-2 rounded-md mb-4" onClick={() => navigate('/service')}>문의하기</button>
            </div>
            <div className="space-y-4 min-h-[632px]">
                <div className="hidden md:grid grid-cols-7 font-semibold bg-gray-100 p-3 rounded text-center mt-0 border border-gray-200">
                    <div>번호</div>
                    <div className='col-span-3'>제목</div>
                    <div>작성자</div>
                    <div>작성일</div>
                    <div>상태</div>
                </div>
                {displayPosts.map((post, index) => (
                    post ? (
                        <div 
                            key={`post-${post.boardIdx}`} 
                            className="hidden md:grid grid-cols-7 p-3 hover:bg-gray-50 transition-colors text-center cursor-pointer mt-0 rounded border border-gray-200"
                            onClick={() => handleItemClick(post.boardIdx)}
                        >
                            <div className='flex justify-center'>{indexOfFirstPost + index + 1}</div>
                            <div className='col-span-3'>{post.title}</div>
                            <div>{post.userId}</div>
                            <div>{post.createdAt}</div>
                            <div>{post.status}</div>
                        </div>
                    ) : (
                        <div 
                            key={`empty-${index}`} 
                            className="hidden md:grid grid-cols-7 p-3 text-center mt-0 rounded border border-gray-200"
                        >
                            <div className='flex justify-center'>-</div>
                            <div className='col-span-3'>-</div>
                            <div>-</div>
                            <div>-</div>
                            <div>-</div>
                        </div>
                    )
                ))}
                {displayPosts.map((post, index) => (
                    post ? (
                        <div 
                            key={`post-mobile-${post.boardIdx}`} 
                            className="md:hidden bg-white shadow-sm rounded-lg overflow-hidden p-4 text-center cursor-pointer border border-gray-200"
                            onClick={() => handleItemClick(post.boardIdx)}
                        >
                            <div className="grid grid-cols-2 gap-2">
                                <div className="font-semibold">번호:</div>
                                <div className='flex justify-center'>{indexOfFirstPost + index + 1}</div>
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
                    ) : (
                        <div 
                            key={`empty-mobile-${index}`} 
                            className="md:hidden bg-white shadow-sm rounded-lg overflow-hidden p-4 text-center"
                        >
                            <div className="grid grid-cols-2 gap-2">
                                <div className="font-semibold">번호:</div>
                                <div className='flex justify-center'>-</div>
                                <div className="font-semibold">제목:</div>
                                <div className="font-medium">-</div>
                                <div className="font-semibold">작성자:</div>
                                <div>-</div>
                                <div className="font-semibold">작성일:</div>
                                <div>-</div>
                                <div className="font-semibold">상태:</div>
                                <div>-</div>
                            </div>
                        </div>
                    )
                ))}
                <div className="flex justify-center">
                    <button
                        className={`px-3 py-1 mx-1 ${currentPage === 1 ? 'bg-gray-300' : 'bg-gray-200'}`}
                        onClick={() => handlePageChange(currentPage - 1)}
                        disabled={currentPage === 1}
                    >
                        이전
                    </button>
                    {Array.from({ length: Math.ceil(posts.length / postsPerPage) }, (_, i) => (
                        <button
                            key={i}
                            className={`px-3 py-1 mx-1 ${currentPage === i + 1 ? 'bg-blue-500 text-white' : 'bg-gray-200'}`}
                            onClick={() => handlePageChange(i + 1)}
                        >
                            {i + 1}
                        </button>
                    ))}
                    <button
                        className={`px-3 py-1 mx-1 ${currentPage === Math.ceil(posts.length / postsPerPage) ? 'bg-gray-300' : 'bg-gray-200'}`}
                        onClick={() => handlePageChange(currentPage + 1)}
                        disabled={currentPage === Math.ceil(posts.length / postsPerPage)}
                    >
                        다음
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ServiceCenter;
