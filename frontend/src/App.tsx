import { useEffect } from 'react';
import { Route, Routes, useLocation } from 'react-router-dom';
import Application from './Pages/Application/Application';
import ServiceCenter from './Pages/ServiceCenter/ServiceCenter';
import Main from './Pages/Main/Main';
import Login from './Pages/auth/Login/Login';
import Signup from './Pages/auth/Signup/Signup';
import Header from './Components/Header';
import FindAccount from './Pages/auth/FindAccount/FindAccount';
import Profile from './Pages/auth/Profile/Profile';
import Result from './Pages/Result/Result';
import Mydocument from './Pages/Mydocument/Mydocument';
import Detail from './Pages/ServiceCenter/Detail';
import DocumentType from './Pages/DocumentType/DocumentType';
import DocumentDetail from './Pages/Mydocument/DocumentDetail';
import DocumentEdit from './Pages/Mydocument/DocumentUpdate';
import Service from './Pages/ServiceCenter/Service';
import ResetPassword from './Pages/auth/FindAccount/ResetPassword';
import Footer from './Components/footer';

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
    <>
      <Routes>
        <Route path="/" element={<><Header /><Main /></>} />
        <Route element={<><Header /><Footer /></>}>
          <Route path="application" element={<Application />} />
          <Route path="result" element={<Result />} />
          <Route path="servicecenter" element={<ServiceCenter />} />
          <Route path="service" element={<Service />} />
          <Route path="servicecenter/detail/:boardIdx" element={<Detail />} />
          <Route path="mydocument" element={<Mydocument />} />
          <Route path="mydocument/detail/:ocrId" element={<DocumentDetail />} />
          <Route path="mydocument/edit/:ocrId" element={<DocumentEdit />} />
          <Route path="profile" element={<Profile />} />
          <Route path="documenttype" element={<DocumentType />} />
        </Route>
        <Route path="/auth">
          <Route path="login" element={<Login />} />
          <Route path="signup" element={<Signup />} />
          <Route path="find-account" element={<FindAccount />} />
          <Route path="reset-password/:token" element={<ResetPassword />} />
        </Route>
      </Routes>
    </>
  );
}

export default App;
