import { useEffect } from 'react';
import { Route, Routes, useLocation, Outlet } from 'react-router-dom';
import Application from './Pages/Application/Application';
import ServiceCenter from './Pages/ServiceCenter/ServiceCenter';
import Main from './Pages/Main/Main';
import Login from './Pages/auth/Login/Login';
import Signup from './Pages/auth/Signup/Signup';
import Header from './Components/Header';
import FindAccount from './Pages/auth/FindAccount/FindAccount';
import Profile from './Pages/auth/Profile/Profile';
import InquiryList from './Components/InquiryList/InquiryList';
import InquiryDetail from './Components/InquiryDetail/InquiryDetail';
import InquiryForm from './Components/InquiryForm/InquiryForm';
import Result from './Pages/Result/Result';

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
        <Route path="result" element={<Result />} />
        <Route path="servicecenter" element={<ServiceCenter />}>
          <Route path="list" element={<InquiryList/>} />
          <Route path="list/:id" element={<InquiryDetail />} />
          <Route path="write" element={<InquiryForm />} />
        </Route>
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