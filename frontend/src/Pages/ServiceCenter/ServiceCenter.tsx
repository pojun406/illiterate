import React, { useState } from 'react';
import Sidebar from '../../Components/Sidebar/Sidebar';
import { Outlet } from 'react-router-dom';

const ServiceCenter = () => {
    const [sidebarOpen, setSidebarOpen] = useState(false);

    return (
        <div className="flex p-6 bg-gray-100" style={{ userSelect: 'none' }}>
            <Sidebar sidebarOpen={sidebarOpen} setSidebarOpen={setSidebarOpen} />
            <div className={`flex-auto bg-white rounded-lg shadow-md md:mx-4 transition-all ${sidebarOpen ? 'ml-64' : 'ml-0'} lg:ml-2`}>
                <Outlet/>
            </div>
        </div>
    );
};

export default ServiceCenter;
