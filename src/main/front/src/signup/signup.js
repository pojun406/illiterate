import React, { useState } from 'react';

function Signup({ onClose }) {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const handleSignup = (event) => {
        event.preventDefault();
        // 여기에 회원가입 처리 로직을 추가할 수 있습니다.
        // 이 예시에서는 간단하게 콘솔에 회원가입 정보를 출력합니다.
        console.log("Username:", username);
        console.log("Email:", email);
        console.log("Password:", password);
        // 회원가입 성공 시 폼을 닫습니다.
        onClose();
    };

    return (
        <div>
            <h2>Sign Up</h2>
            <form onSubmit={handleSignup}>
                <label>
                    Username:
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                </label>
                <br />
                <label>
                    Email:
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                </label>
                <br />
                <label>
                    Password:
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </label>
                <br />
                <button type="submit">Sign Up</button>
            </form>
        </div>
    );
}

export default Signup;
