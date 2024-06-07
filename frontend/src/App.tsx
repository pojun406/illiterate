import { useEffect } from 'react';
import { Route, Routes, useLocation } from 'react-router-dom';
import Application from './Pages/Application/Application';
import ServiceCenter from './Pages/ServiceCenter/ServiceCenter';
import Result from './Pages/Result/Result';
import Main from './Pages/Main/Main';
import Login from './Pages/auth/Login/Login';
import Signup from './Pages/auth/Signup/Signup';
import Header from './Components/Header';
import FindAccount from './Pages/auth/FindAccount/FindAccount';
import Profile from './Pages/auth/Profile/Profile';

function App() {
  const location = useLocation();

  useEffect(() => {
    if (location.pathname === '/') {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'auto';
    }
  }, [location.pathname]);

  return (
    <Routes>
      <Route element={<Header/>}>
        <Route path="application" element={<Application />} />
        <Route path="servicecenter" element={<ServiceCenter />} />
        <Route path='result' element={<Result/>} />
        <Route path="/" element={<Main/>}/>
        <Route path="profile" element={<Profile />} />
      </Route>
      <Route path="/auth">
        <Route path="login" element={<Login />} />
        <Route path="signup" element={<Signup />} />
        <Route path="find-account" element={<FindAccount />} />
      </Route>
    </Routes>
  );
}

export default App;