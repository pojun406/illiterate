import React from 'react';
import { Routes, Route, Outlet } from 'react-router-dom';
import Main from './main/main';
import LoginForm from './login/login';
import SignupForm from './signup/signup';

function AppRouter() {
    return (
        <Routes>
            <Route path="*" element={<Main />} />
            <Route path="/sign/*" element={<SignRouter />} />
        </Routes>
    );
}

function SignRouter() {
    return (
        <Outlet>
            <Route path="login" element={<LoginForm />} />
            <Route path="signup" element={<SignupForm />} />
        </Outlet>
    );
}

export default AppRouter;
