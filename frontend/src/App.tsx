import React from 'react';
import {Route, Routes} from "react-router-dom";
import Main from "./Pages/Main";
import Header from "./Components/Header";
import ServiceCenter from "./Pages/ServiceCenter/ServiceCenter";
import Application from "./Pages/Application/Application";
import Login from "./Pages/auth/Login";
import Signup from "./Pages/auth/Signup";

function App() {
  return (
    <Routes>
        <Route element={<Header />}>
            <Route path="application" element={<Application />} />
            <Route path="servicecenter" element={<ServiceCenter />} />
        </Route>
        <Route path="/" element={<Main/>}/>

        <Route path="/auth">
            <Route path="login" element={<Login />} />
            <Route path="signup" element={<Signup />} />
        </Route>
    </Routes>
  );
}

export default App;
