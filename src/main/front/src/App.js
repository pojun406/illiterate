import './App.css';
import React from "react";
import { BrowserRouter as Router, Link, Routes, Route } from 'react-router-dom';
import AppRouter from "./AppRouter";

function App() {
    return (
        <div className="App">
            <Router>
                <div>
                    <button><Link to="/">홈으로</Link></button>
                    <button><Link to="/sign/login">로그인</Link></button>
                    <button><Link to="/sign/signup">회원가입</Link></button>
                </div>
                <Routes>
                    <Route path="*" element={<AppRouter />} />
                </Routes>
            </Router>
        </div>
    );
}

export default App;