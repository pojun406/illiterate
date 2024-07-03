import React, { useState, useEffect, useRef } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import fetchWithAuth from '../../../Components/AccessToken/AccessToken';
import axios from 'axios';
import { IoEye, IoEyeOff } from 'react-icons/io5';

const Profile: React.FC = () => {
    const [password, setPassword] = useState<string>('');
    const [currentPassword, setCurrentPassword] = useState<string>('');
    const [newPassword, setNewPassword] = useState<string>('');
    const [confirmNewPassword, setConfirmNewPassword] = useState<string>('');
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
    const [activeTab, setActiveTab] = useState<string>('profile');
    const [userInfo, setUserInfo] = useState({
        id: '',
        name: '',
        email: '',
        password: ''
    });
    const [editUserInfo, setEditUserInfo] = useState({
        name: '',
        email: ''
    });
    const [modalMessage, setModalMessage] = useState<string>('');
    const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState<boolean>(false);
    const [isUnsavedChangesModalOpen, setIsUnsavedChangesModalOpen] = useState<boolean>(false);
    const hasFetchedUserInfo = useRef(false);
    const [pendingTab, setPendingTab] = useState<string>('');
    const [showCurrentPassword, setShowCurrentPassword] = useState<boolean>(false);
    const [showNewPassword, setShowNewPassword] = useState<boolean>(false);
    const [showConfirmNewPassword, setShowConfirmNewPassword] = useState<boolean>(false);
    const [newPasswordError, setNewPasswordError] = useState<string>('');
    const [confirmNewPasswordError, setConfirmNewPasswordError] = useState<string>('');

    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                const userid = localStorage.getItem("id");
                const response = await fetchWithAuth(`/userinfo/${userid}`);
                if (typeof response !== 'string' && response.status === 200) {
                    const data = response.data;
                    console.log(data); // 콘솔에 출력
                    setUserInfo({
                        id: data.data.id,
                        name: data.data.name,
                        email: data.data.email,
                        password: '' 
                    });
                    setEditUserInfo({
                        name: data.data.name,
                        email: data.data.email
                    });
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

        if (!hasFetchedUserInfo.current) {
            fetchUserInfo();
            hasFetchedUserInfo.current = true;
        }
    }, []);

    useEffect(() => {
        setIsModalOpen(false);
        setIsDeleteModalOpen(false); // URL 변경 시 모달 닫기
    }, [location.pathname]);

    const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setPassword(e.target.value);
    };

    const handleCurrentPasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setCurrentPassword(e.target.value);
    };

    const handleNewPasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        setNewPassword(value);
        setNewPasswordError(""); // 입력 중일 때는 에러 메시지를 지웁니다.
    };

    const handleNewPasswordBlur = () => {
        validateNewPassword();
    };

    const handleConfirmNewPasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        setConfirmNewPassword(value);
        setConfirmNewPasswordError(""); // 입력 중일 때는 에러 메시지를 지웁니다.
    };

    const handleConfirmNewPasswordBlur = () => {
        validateConfirmNewPassword();
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
        setEditUserInfo({
            ...editUserInfo,
            [e.target.name]: e.target.value
        });
    };

    const handleUserInfoSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        try {
            const userId = localStorage.getItem('id');
            const token = localStorage.getItem('accessToken');
            const response = await fetch(`/userUpdate/${userId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(editUserInfo)
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

    const handleAccountDeletion = async () => {
        try {
            const userId = localStorage.getItem('id');
            const response = await fetch(`/deluser/${userId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ userId })
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

    const handleTabChange = (tab: string) => {
        if (activeTab === 'edit' && (editUserInfo.name !== userInfo.name || editUserInfo.email !== userInfo.email)) {
            setPendingTab(tab);
            setIsUnsavedChangesModalOpen(true);
        } else {
            setActiveTab(tab);
        }
    };

    const confirmTabChange = () => {
        setIsUnsavedChangesModalOpen(false);
        setActiveTab(pendingTab);
        setEditUserInfo({
            name: userInfo.name,
            email: userInfo.email
        });
    };

    const validateNewPassword = () => {
        const regex = /^(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[a-z\d@$!%*?&]{8,}$/;
        if (!regex.test(newPassword)) {
            setNewPasswordError("비밀번호는 최소 8자, 하나 이상의 소문자, 숫자, 특수문자가 포함되어야 합니다.");
            return false;
        }
        return true;
    };

    const validateConfirmNewPassword = () => {
        if (newPassword !== confirmNewPassword) {
            setConfirmNewPasswordError("새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            return false;
        }
        return true;
    };

    const handleChangePasswordSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (validateNewPassword() && validateConfirmNewPassword()) {
            if (userInfo.password !== currentPassword) {
                setModalMessage('현재 비밀번호가 일치하지 않습니다.');
                setIsModalOpen(true);
                return;
            }
            try {
                const userId = localStorage.getItem('id');
                await axios.post(`/userUpdate/${userId}/password`, 
                { newPassword }, 
                {
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('accessToken')}`,
                        'Content-Type': 'application/json'
                    }
                });
                setModalMessage('비밀번호가 성공적으로 변경되었습니다.');
                setIsModalOpen(true);
            } catch (error) {
                console.error('Error:', error);
                setModalMessage('서버와의 통신 중 오류가 발생했습니다. 다시 시도해주세요.');
                setIsModalOpen(true);
            }
        }
    };

    const renderContent = () => {
        switch (activeTab) {
            case 'profile':
                return (
                    <div>
                        <h2 className="text-2xl font-semibold mb-6">프로필</h2>
                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="name">
                                이름
                            </label>
                            <p className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
                                {userInfo.name}
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
                        <div className="mb-4">
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="id">
                                아이디
                            </label>
                            <p className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
                                {userInfo.id}
                            </p>
                        </div>
                        <button
                            onClick={() => handleTabChange('edit')}
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
                            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="name">
                                이름
                            </label>
                            <input
                                type="text"
                                id="name"
                                name="name"
                                value={editUserInfo.name}
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
                                value={editUserInfo.email}
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
            case 'changePassword':
                return (
                    <div>
                        <h2 className="text-2xl font-semibold mb-6">비밀번호 변경</h2>
                        <form onSubmit={handleChangePasswordSubmit}>
                            <div className="mb-4 relative">
                                <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="currentPassword">
                                    현재 비밀번호
                                </label>
                                <div className="flex items-center relative">
                                    <input
                                        type={showCurrentPassword ? "text" : "password"}
                                        id="currentPassword"
                                        value={currentPassword}
                                        onChange={handleCurrentPasswordChange}
                                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline pr-10"
                                        required
                                    />
                                    <span
                                        onClick={() => setShowCurrentPassword(!showCurrentPassword)}
                                        className="absolute right-3 cursor-pointer select-none"
                                        style={{ userSelect: 'none' }}
                                    >
                                        {showCurrentPassword ? <IoEye /> : <IoEyeOff />}
                                    </span>
                                </div>
                            </div>
                            <div className="mb-4 relative">
                                <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="newPassword">
                                    새 비밀번호
                                </label>
                                <div className="flex items-center relative">
                                    <input
                                        type={showNewPassword ? "text" : "password"}
                                        id="newPassword"
                                        value={newPassword}
                                        onChange={handleNewPasswordChange}
                                        onBlur={handleNewPasswordBlur}
                                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline pr-10"
                                        required
                                    />
                                    <span
                                        onClick={() => setShowNewPassword(!showNewPassword)}
                                        className="absolute right-3 cursor-pointer select-none"
                                        style={{ userSelect: 'none' }}
                                    >
                                        {showNewPassword ? <IoEye /> : <IoEyeOff />}
                                    </span>
                                </div>
                                {newPasswordError && (
                                    <div className="text-red-500 text-xs mt-1">
                                        {newPasswordError}
                                    </div>
                                )}
                            </div>
                            <div className="mb-4 relative">
                                <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="confirmNewPassword">
                                    새 비밀번호 확인
                                </label>
                                <div className="flex items-center relative">
                                    <input
                                        type={showConfirmNewPassword ? "text" : "password"}
                                        id="confirmNewPassword"
                                        value={confirmNewPassword}
                                        onChange={handleConfirmNewPasswordChange}
                                        onBlur={handleConfirmNewPasswordBlur}
                                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline pr-10"
                                        required
                                    />
                                    <span
                                        onClick={() => setShowConfirmNewPassword(!showConfirmNewPassword)}
                                        className="absolute right-3 cursor-pointer select-none"
                                        style={{ userSelect: 'none' }}
                                    >
                                        {showConfirmNewPassword ? <IoEye /> : <IoEyeOff />}
                                    </span>
                                </div>
                                {confirmNewPasswordError && (
                                    <div className="text-red-500 text-xs mt-1">
                                        {confirmNewPasswordError}
                                    </div>
                                )}
                            </div>
                            <button
                                type="submit"
                                className="w-full px-4 py-2 text-white bg-blue-700 rounded-md hover:bg-blue-800 focus:outline-none"
                            >
                                비밀번호 변경
                            </button>
                        </form>
                    </div>
                );
            case 'delete':
                return (
                    <div>
                        <h2 className="text-2xl font-semibold mb-6">회원 탈퇴</h2>
                        <p className="mb-4">정말 회원 탈퇴를 하시겠습니까?</p>
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
                            onClick={() => handleTabChange('profile')}
                        >
                            프로필
                        </li>
                        <li
                            className={`cursor-pointer py-2 ${activeTab === 'edit' ? 'font-bold' : ''}`}
                            onClick={() => handleTabChange('edit')}
                        >
                            정보 수정
                        </li>
                        <li
                            className={`cursor-pointer py-2 ${activeTab === 'changePassword' ? 'font-bold' : ''}`}
                            onClick={() => handleTabChange('changePassword')}
                        >
                            비밀번호 변경
                        </li>
                        <li
                            className={`cursor-pointer py-2 ${activeTab === 'delete' ? 'font-bold' : ''}`}
                            onClick={() => handleTabChange('delete')}
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
                        <p>정말 회원 탈퇴 하시겠습니까?</p>
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
            {isUnsavedChangesModalOpen && (
                <div className="fixed inset-0 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded shadow-lg relative z-50" onClick={(e) => e.stopPropagation()}>
                        <p>변경사항이 저장되지 않았습니다.</p>
                        <p>그래도 이동하시겠습니까?</p>
                        <div className="mt-4 flex justify-between">
                            <button
                                onClick={() => setIsUnsavedChangesModalOpen(false)}
                                className="px-4 py-2 bg-gray-300 text-black rounded hover:bg-gray-400"
                            >
                                취소
                            </button>
                            <button
                                onClick={confirmTabChange}
                                className="px-4 py-2 bg-red-700 text-white rounded hover:bg-red-800"
                            >
                                확인
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
