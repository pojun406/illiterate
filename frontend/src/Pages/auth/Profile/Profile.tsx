import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const Profile: React.FC = () => {
    const [password, setPassword] = useState<string>('');
    const [currentPassword, setCurrentPassword] = useState<string>('');
    const [newPassword, setNewPassword] = useState<string>('');
    const [confirmNewPassword, setConfirmNewPassword] = useState<string>('');
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
    const [activeTab, setActiveTab] = useState<string>('profile');
    const [userInfo, setUserInfo] = useState({
        userid: '',
        username: '',
        email: '',
        password: ''
    });
    const [modalMessage, setModalMessage] = useState<string>('');
    const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState<boolean>(false);

    const navigate = useNavigate();

    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                const response = await fetch('/mockup/profile.json');
                if (response.ok) {
                    const data = await response.json();
                    setUserInfo(data);
                } else {
                    setModalMessage('사용자 정보를 가져오는데 실패했습니다.');
                    setIsModalOpen(true);
                }
            } catch (error) {
                console.error('Error:', error);
                setModalMessage('서버와의 통신 중 오류가 발생했습니다. 다시 시도해주세요.');
                setIsModalOpen(true);
            }
        };

        fetchUserInfo();
    }, []);

    const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setPassword(e.target.value);
    };

    const handleCurrentPasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setCurrentPassword(e.target.value);
    };

    const handleNewPasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setNewPassword(e.target.value);
    };

    const handleConfirmNewPasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setConfirmNewPassword(e.target.value);
    };

    const handlePasswordSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (password === '1234') {
            setIsAuthenticated(true);
        } else {
            setModalMessage('비밀번호가 일치하지 않습니다.');
            setIsModalOpen(true);
        }
    };

    const handleUserInfoChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setUserInfo({
            ...userInfo,
            [e.target.name]: e.target.value
        });
    };

    const handleUserInfoSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        try {
            const response = await fetch('/update-profile', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userInfo)
            });

            if (response.ok) {
                setModalMessage('정보가 성공적으로 수정되었습니다.');
                setIsModalOpen(true);
                navigate('/');
            } else {
                setModalMessage('정보 수정에 실패했습니다. 다시 시도해주세요.');
                setIsModalOpen(true);
            }
        } catch (error) {
            console.error('Error:', error);
            setModalMessage('서버와의 통신 중 오류가 발생했습니다. 다시 시도해주세요.');
            setIsModalOpen(true);
        }
    };

    const handlePasswordUpdateSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (currentPassword !== userInfo.password) {
            setModalMessage('현재 비밀번호가 일치하지 않습니다.');
            setIsModalOpen(true);
            return;
        }
        if (newPassword !== confirmNewPassword) {
            setModalMessage('새 비밀번호가 일치하지 않습니다.');
            setIsModalOpen(true);
            return;
        }
        try {
            const response = await fetch('/update-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ password: newPassword })
            });

            if (response.ok) {
                setModalMessage('비밀번호가 성공적으로 수정되었습니다.');
                setIsModalOpen(true);
                navigate('/');
            } else {
                setModalMessage('비밀번호 수정에 실패했습니다. 다시 시도해주세요.');
                setIsModalOpen(true);
            }
        } catch (error) {
            console.error('Error:', error);
            setModalMessage('서버와의 통신 중 오류가 발생했습니다. 다시 시도해주세요.');
            setIsModalOpen(true);
        }
    };

    const handleAccountDeletion = async () => {
        try {
            const response = await fetch('/delete-account', {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                setModalMessage('계정이 성공적으로 삭제되었습니다.');
                setIsModalOpen(true);
                navigate('/');
            } else {
                setModalMessage('계정 삭제에 실패했습니다. 다시 시도해주세요.');
                setIsModalOpen(true);
            }
        } catch (error) {
            console.error('Error:', error);
            setModalMessage('서버와의 통신 중 오류가 발생했습니다. 다시 시도해주세요.');
            setIsModalOpen(true);
        }
    };

    const renderContent = () => {
        switch (activeTab) {
            case 'profile':
                return (
                    <div>
                        <h2 className="text-2xl font-semibold mb-6">프로필</h2>
                        <div className="flex items-center mb-6">
                            <img
                                className="w-16 h-16 rounded-full mr-4"
                                src="https://via.placeholder.com/150"
                                alt="User Avatar"
                            />
                            <div>
                                <h3 className="text-xl font-semibold">{userInfo.username}</h3>
                                <p className="text-gray-600">{userInfo.email}</p>
                            </div>
                        </div>
                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="userid">
                                아이디
                            </label>
                            <p className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
                                {userInfo.userid}
                            </p>
                        </div>
                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-gray-700 text-sm font-bold mb-2" htmlFor="username">
                                이름
                            </label>
                            <p className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
                                {userInfo.username}
                            </p>
                        </div>
                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="email">
                                이메일
                            </label>
                            <p className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
                                {userInfo.email}
                            </p>
                        </div>
                        <button
                            onClick={() => setActiveTab('edit')}
                            className="w-full px-4 py-2 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none"
                        >
                            정보 수정
                        </button>
                    </div>
                );
            case 'edit':
                return !isAuthenticated ? (
                    <form onSubmit={handlePasswordSubmit}>
                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="password">
                                비밀번호 확인
                            </label>
                            <input
                                type="password"
                                id="password"
                                value={password}
                                onChange={handlePasswordChange}
                                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                required
                            />
                        </div>
                        <button
                            type="submit"
                            className="w-full px-4 py-2 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none"
                        >
                            확인
                        </button>
                    </form>
                ) : (
                    <>
                    <form onSubmit={handleUserInfoSubmit}>
                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="userid">
                                아이디
                            </label>
                            <input
                                type="text"
                                id="userid"
                                name="userid"
                                value={userInfo.userid}
                                onChange={handleUserInfoChange}
                                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                required
                            />
                        </div>
                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="username">
                                이름
                            </label>
                            <input
                                type="text"
                                id="username"
                                name="username"
                                value={userInfo.username}
                                onChange={handleUserInfoChange}
                                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                required
                            />
                        </div>
                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="email">
                                이메일
                            </label>
                            <input
                                type="email"
                                id="email"
                                name="email"
                                value={userInfo.email}
                                onChange={handleUserInfoChange}
                                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                required
                            />
                        </div>
                        <button
                            type="submit"
                            className="w-full px-4 py-2 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none"
                        >
                            수정
                        </button>
                    </form>
                    </>
                );
            case 'password':
                return (
                    <form onSubmit={handlePasswordUpdateSubmit}>
                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="currentPassword">
                                현재 비밀번호
                            </label>
                            <input
                                type="password"
                                id="currentPassword"
                                name="currentPassword"
                                value={currentPassword}
                                onChange={handleCurrentPasswordChange}
                                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                required
                            />
                        </div>
                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="newPassword">
                                새 비밀번호
                            </label>
                            <input
                                type="password"
                                id="newPassword"
                                name="newPassword"
                                value={newPassword}
                                onChange={handleNewPasswordChange}
                                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                required
                            />
                        </div>
                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="confirmNewPassword">
                                새 비밀번호 확인
                            </label>
                            <input
                                type="password"
                                id="confirmNewPassword"
                                name="confirmNewPassword"
                                value={confirmNewPassword}
                                onChange={handleConfirmNewPasswordChange}
                                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                                required
                            />
                        </div>
                        <button
                            type="submit"
                            className="w-full px-4 py-2 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none"
                        >
                            비밀번호 수정
                        </button>
                    </form>
                );
            case 'delete':
                return (
                    <div>
                        <h2 className="text-2xl font-semibold mb-6">회원 탈퇴</h2>
                        <p className="mb-4">정말로 회원 탈퇴를 하시겠습니까?</p>
                        <button
                            onClick={() => setIsDeleteModalOpen(true)}
                            className="w-full px-4 py-2 text-white bg-red-700 rounded-md hover:bg-red-800 focus:outline-none"
                        >
                            회원 탈퇴
                        </button>
                    </div>
                );
            default:
                return null;
        }
    };

    return (
        <div className="min-h-screen bg-gray-100">
            <div className="container mx-auto py-10 flex">
                <div className="w-1/4 flex-shrink-0 bg-white shadow-md rounded-lg p-6 mr-6" style={{ height: 'fit-content' }}>
                    <ul>
                        <li
                            className={`cursor-pointer py-2 ${activeTab === 'profile' ? 'font-bold' : ''}`}
                            onClick={() => setActiveTab('profile')}
                        >
                            프로필
                        </li>
                        <li
                            className={`cursor-pointer py-2 ${activeTab === 'edit' ? 'font-bold' : ''}`}
                            onClick={() => setActiveTab('edit')}
                        >
                            정보 수정
                        </li>
                        <li
                            className={`cursor-pointer py-2 ${activeTab === 'password' ? 'font-bold' : ''}`}
                            onClick={() => setActiveTab('password')}
                        >
                            비밀번호 수정
                        </li>
                        <li
                            className={`cursor-pointer py-2 ${activeTab === 'delete' ? 'font-bold' : ''}`}
                            onClick={() => setActiveTab('delete')}
                        >
                            회원 탈퇴
                        </li>
                    </ul>
                </div>
                <div className="w-3/4 bg-white shadow-md rounded-lg p-6">
                    {renderContent()}
                </div>
            </div>
            {isModalOpen && (
                <div className="fixed inset-0 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded shadow-lg relative z-50" onClick={(e) => e.stopPropagation()}>
                        <p>{modalMessage}</p>
                        <div className="mt-4 flex justify-center">
                            <button
                                onClick={() => setIsModalOpen(false)}
                                className="w-full px-4 py-2 bg-blue-700 text-white rounded hover:bg-blue-800"
                            >
                                닫기
                            </button>
                        </div>
                    </div>
                    <div className="fixed inset-0 bg-black opacity-50 z-40"></div>
                </div>
            )}
            {isDeleteModalOpen && (
                <div className="fixed inset-0 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded shadow-lg relative z-50" onClick={(e) => e.stopPropagation()}>
                        <p>저장되어있는 문서데이터와 계정정보가 모두 사라집니다. </p>
                        <p>정말로 회원 탈퇴를 하시겠습니까?</p>
                        <div className="mt-4 flex justify-between">
                            <button
                                onClick={() => setIsDeleteModalOpen(false)}
                                className="px-4 py-2 bg-gray-300 text-black rounded hover:bg-gray-400"
                            >
                                취소
                            </button>
                            <button
                                onClick={handleAccountDeletion}
                                className="px-4 py-2 bg-red-700 text-white rounded hover:bg-red-800"
                            >
                                회원 탈퇴
                            </button>
                        </div>
                    </div>
                    <div className="fixed inset-0 bg-black opacity-50 z-40"></div>
                </div>
            )}
        </div>
    );
};

export default Profile;
