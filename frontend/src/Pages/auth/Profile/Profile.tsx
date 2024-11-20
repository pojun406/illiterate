import fetchWithAuth from '../../../Components/AccessToken/AccessToken';
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const Profile = () => {
  const [activeTab, setActiveTab] = useState('profile');

    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [userid, setUserId] = useState('');
    const [editName, setEditName] = useState('');
    const [editEmail, setEditEmail] = useState('');
    const navigate = useNavigate();
    useEffect(() => {
        getProfile();
    }, []);
    
    function getProfile() {
        fetchWithAuth('/user/userinfo')
        .then(res => {
            if (typeof res === 'object' && 'data' in res) {
                const { name, email, userid } = res.data.data;
                console.log(name, email, userid);
                setName(name);
                setEmail(email);
                setUserId(userid);
                setEditName(name);
                setEditEmail(email);
            } else {
                console.error('Unexpected response format:', res);
            }
        })
        .catch(error => {
            console.error('프로필 정보를 가져오는 중 오류 발생:', error);
        });
    }

function updateProfile() {
    setName(editName);
    setEmail(editEmail);
    fetchWithAuth('/user/userUpdate', { name: editName, email: editEmail })
    .then(res => {
        if (typeof res === 'object' && 'data' in res) {
            return res.data;
        } else {
            throw new Error('Unexpected response format');
        }
    })
    .then(data => {
        if (data && data.code === 200) {
            console.log('프로필 업데이트 성공:', data);
            getProfile();
            setActiveTab('profile');
        } else {
            console.error('프로필 업데이트 실패:', data.message);
        }
    })
    .catch(error => {
        console.error('프로필 업데이트 중 오류 발생:', error);
    });
}

    function EditNameHandler(e: React.ChangeEvent<HTMLInputElement>) {
        setEditName(e.target.value);
    }

    function EditEmailHandler(e: React.ChangeEvent<HTMLInputElement>) {
        setEditEmail(e.target.value);
    }

    function deleteUser() {
      const userIndex = localStorage.getItem('id') || '';
        fetchWithAuth(`/user/deluser/${userIndex}`)
        .then(res => {
            console.log(res);
            localStorage.clear();
            navigate('/');
        })
        .catch(error => {
            console.error('회원 탈퇴 중 오류 발생:', error);
        });
    }

  return (
    <div className='flex flex-col w-[1260px] h-full mx-auto my-4'>
      <div className="flex flex-col md:flex-row">
        <nav className="md:w-1/4 mr-4 bg-white rounded-lg p-4">
          <ul className="list-none p-0">
            <li className={`py-2 px-4 border-b cursor-pointer ${activeTab === 'profile' ? 'bg-blue-100 text-blue-700' : 'hover:bg-gray-100'}`} onClick={() => setActiveTab('profile')}>프로필</li>
            <li className={`py-2 px-4 border-b cursor-pointer ${activeTab === 'edit' ? 'bg-blue-100 text-blue-700' : 'hover:bg-gray-100'}`} onClick={() => setActiveTab('edit')}>정보 수정</li>
            <li className={`py-2 px-4 border-b cursor-pointer ${activeTab === 'password' ? 'bg-blue-100 text-blue-700' : 'hover:bg-gray-100'}`} onClick={() => setActiveTab('password')}>비밀번호 변경</li>
            <li className={`py-2 px-4 border-b cursor-pointer ${activeTab === 'delete' ? 'bg-blue-100 text-blue-700' : 'hover:bg-gray-100'}`} onClick={() => setActiveTab('delete')}>회원 탈퇴</li>
          </ul>
        </nav>
        <div className="md:w-3/4 bg-white rounded-lg p-6">
          {activeTab === 'profile' && (
            <div className="card mb-4">
              <div className="bg-gray-100 p-4 text-xl font-bold">프로필</div>
              <div className="p-4">
                <form>
                  <div className="mb-3">
                    <label className="block font-bold">아이디</label>
                    <input type="text" className="form-control rounded-md border border-gray-300 mt-2 p-1 px-2 w-full focus:outline-none hover:cursor-default" id="id" readOnly value={userid} />
                  </div>
                  <div className="mb-3">
                    <label className="block font-bold mb-1">이름</label>
                    <input type="text" className="form-control rounded-md border border-gray-300 mt-2 p-1 px-2 w-full focus:outline-none hover:cursor-default" id="name" readOnly value={name} />
                  </div>
                  <div className="mb-3">
                    <label className="block font-bold">이메일</label>
                    <input type="email" className="form-control rounded-md border border-gray-300 mt-2 p-1 px-2 w-full focus:outline-none hover:cursor-default" id="email" readOnly value={email} />
                  </div>
                  <button type="button" className="btn bg-blue-500 rounded-md mt-3 text-white hover:bg-blue-600 w-full h-8" onClick={() => setActiveTab('edit')}>정보 수정</button>
                </form>
              </div>
            </div>
          )}
          {activeTab === 'edit' && (
            <div className="card mb-4">
              <div className="bg-gray-100 p-4 text-xl font-bold">정보 수정</div>
              <div className="p-4">
                <form>
                  <div className="mb-3">
                    <label className="block font-bold mb-1">이름</label>
                    <input
                      type="text"
                      className="form-control rounded-md border border-gray-300 mt-2 p-1 px-2 w-full hover:cursor-default"
                      id="EditName"
                      value={editName}
                      onChange={EditNameHandler}
                    />
                  </div>
                  <div className="mb-3">
                    <label className="block font-bold">이메일</label>
                    <input
                      type="email"
                      className="form-control rounded-md border border-gray-300 mt-2 p-1 px-2 w-full hover:cursor-default"
                      id="EditEmail"
                      value={editEmail}
                      onChange={EditEmailHandler}
                    />
                  </div>
                  <button type="button" className="btn bg-blue-500 rounded-md mt-3 text-white hover:bg-blue-600 w-full h-8" onClick={updateProfile}>수정하기</button>
                </form>
              </div>
            </div>
          )}
          {activeTab === 'password' && (
            <div className="card mb-4">
              <div className="bg-gray-100 p-4 text-xl font-bold">비밀번호 변경</div>
              <div className="p-4">
                <form>
                  <div className="mb-3">
                    <label className="block font-bold">현재 비밀번호</label>
                    <input type="password" className="form-control rounded-md border border-gray-300 mt-2 p-1 px-2 w-full" />
                  </div>
                  <div className="mb-3">
                    <label className="block font-bold">새 비밀번호</label>
                    <input type="password" className="form-control rounded-md border border-gray-300 mt-2 p-1 px-2 w-full" />
                  </div>
                  <div className="mb-3">
                    <label className="block font-bold">새 비밀번호 확인</label>
                    <input type="password" className="form-control rounded-md border border-gray-300 mt-2 p-1 px-2 w-full" />
                  </div>
                  <button type="button" className="btn bg-blue-500 rounded-md mt-3 text-white hover:bg-blue-600 w-full h-8">비밀번호 변경</button>
                </form>
              </div>
            </div>
          )}
          {activeTab === 'delete' && (
            <div className="card">
              <div className="bg-gray-100 p-4 text-xl font-bold">회원 탈퇴</div>
              <div className="my-4">
                <p className="text-lg font-bold mb-4">정말 회원 탈퇴를 하시겠습니까?</p>
                <button type="button" className="btn rounded-md bg-red-500 text-white hover:bg-red-600 w-full h-8" onClick={deleteUser}>회원 탈퇴</button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Profile;
