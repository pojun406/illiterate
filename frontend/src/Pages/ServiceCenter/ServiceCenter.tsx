import React, { useState, useEffect } from 'react';
import InquiryForm from '../../Components/InquiryForm/InquiryForm';
import InquiryList from '../../Components/InquiryList/InquiryList';
import axios from 'axios';
import { FaBars } from 'react-icons/fa';

const ServiceCenter = () => {
    const [activeTab, setActiveTab] = useState('list');
    const [tickets, setTickets] = useState([]);
    const [sidebarOpen, setSidebarOpen] = useState(false);

    useEffect(() => {
        const fetchTickets = async () => {
            const response = await axios.get('/mockup/tickets.json');
            setTickets(response.data);
        };
        fetchTickets();
    }, []);

    const isActive = (tab: string) => activeTab === tab;

    return (
        <div className="flex min-h-screen bg-gray-100">
            <div className={`fixed inset-0 z-40 bg-black bg-opacity-50 transition-opacity ${sidebarOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'}`} onClick={() => setSidebarOpen(false)}></div>
            <div className={`absolute top-0 left-0 z-50 w-64 h-full bg-white shadow-md rounded-lg p-4 flex flex-col items-center transform ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'} transition-transform md:relative md:translate-x-0 md:w-1/6 md:top-0`}>
                <ul className="w-full">
                    <li className={`cursor-pointer py-6 text-center border-b ${isActive('list') ? 'font-bold' : ''}`} onClick={() => { setActiveTab('list'); setSidebarOpen(false); }}>
                        내 문의사항
                    </li>
                    <li className={`cursor-pointer py-6 text-center border-b ${isActive('write') ? 'font-bold' : ''}`} onClick={() => { setActiveTab('write'); setSidebarOpen(false); }}>
                        문의하기
                    </li>
                </ul>
            </div>
            <div className="flex-1 p-6 bg-white rounded-lg shadow-md ml-4 md:ml-0 md:mx-auto md:max-w-3xl">
                <button className="md:hidden mb-4" onClick={() => setSidebarOpen(true)}>
                    <FaBars size={24} />
                </button>
                {activeTab === 'list' && <InquiryList />}
                {activeTab === 'write' && <InquiryForm />}
            </div>
        </div>
    );
};

export default ServiceCenter;
