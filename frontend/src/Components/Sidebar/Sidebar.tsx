import React, { useRef, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';

interface SidebarProps {
    sidebarOpen: boolean;
    setSidebarOpen: (open: boolean) => void;
}

const Sidebar: React.FC<SidebarProps> = ({ sidebarOpen, setSidebarOpen }) => {
    const location = useLocation();
    const startX = useRef(0);
    const isDragging = useRef(false);
    const isActive = (path: string) => location.pathname === path;

    const isMobileOrTablet = () => {
        return window.innerWidth <= 1024; // 1024px 이하일 때 모바일 또는 태블릿으로 간주
    };

    const handleTouchStart = (e: React.TouchEvent) => {
        if (isMobileOrTablet()) {
            startX.current = e.touches[0].clientX;
        }
    };

    const handleTouchMove = (e: React.TouchEvent) => {
        if (isMobileOrTablet() && isDragging.current) {
            const currentX = e.touches[0].clientX;
            if (currentX - startX.current > 50) {
                setSidebarOpen(true);
            }
        }
    };

    const handleTouchEnd = () => {
        isDragging.current = false;
    };

    useEffect(() => {
        setSidebarOpen(false); // 랜더링될 때 사이드바를 닫음

        const handleMouseDown = (e: MouseEvent) => {
            if (isMobileOrTablet()) {
                startX.current = e.clientX;
                isDragging.current = true;
            }
        };

        const handleMouseMove = (e: MouseEvent) => {
            if (isMobileOrTablet() && isDragging.current) {
                const currentX = e.clientX;
                if (currentX - startX.current > 50) {
                    setSidebarOpen(true);
                }
            }
        };

        const handleMouseUp = () => {
            isDragging.current = false;
        };

        const handleResize = () => {
            if (!isMobileOrTablet()) {
                setSidebarOpen(false);
            }
        };

        if (isMobileOrTablet()) {
            document.addEventListener('mousedown', handleMouseDown);
            document.addEventListener('mousemove', handleMouseMove);
            document.addEventListener('mouseup', handleMouseUp);
        }

        window.addEventListener('resize', handleResize);

        return () => {
            if (isMobileOrTablet()) {
                document.removeEventListener('mousedown', handleMouseDown);
                document.removeEventListener('mousemove', handleMouseMove);
                document.removeEventListener('mouseup', handleMouseUp);
            }
            window.removeEventListener('resize', handleResize);
        };
    }, [setSidebarOpen]);

    const handleOverlayClick = () => {
        if (isMobileOrTablet()) {
            setSidebarOpen(false);
        }
    };

    const renderLinks = () => {
        if (location.pathname.includes('/servicecenter')) {
            return (
                <>
                    <li className={`cursor-pointer py-6 text-center border-b ${isActive('/servicecenter/list') ? 'font-bold' : ''}`}>
                        <Link to="/servicecenter/list" onClick={() => setSidebarOpen(false)}>문의 목록</Link>
                    </li>
                    <li className={`cursor-pointer py-6 text-center border-b ${isActive('/servicecenter/write') ? 'font-bold' : ''}`}>
                        <Link to="/servicecenter/write" onClick={() => setSidebarOpen(false)}>문의하기</Link>
                    </li>
                    <li className={`cursor-pointer py-6 text-center border-b ${isActive('/application') ? 'font-bold' : ''}`}>
                        <Link to="/application" onClick={() => setSidebarOpen(false)}>돌아가기</Link>
                    </li>
                </>
            );
        } else {
            return (
                <>
                    <li className={`cursor-pointer py-6 text-center border-b ${isActive('/application') ? 'font-bold' : ''}`}>
                        <Link to="/application" onClick={() => setSidebarOpen(false)}>등록하기</Link>
                    </li>
                    <li className={`cursor-pointer py-6 text-center border-b ${isActive('/mydocument') ? 'font-bold' : ''}`}>
                        <Link to="/mydocument" onClick={() => setSidebarOpen(false)}>내 문서 보기</Link>
                    </li>
                    <li className={`cursor-pointer py-6 text-center border-b ${isActive('/servicecenter') ? 'font-bold' : ''}`}>
                        <Link to="/servicecenter/list" onClick={() => setSidebarOpen(false)}>고객센터</Link>
                    </li>
                </>
            );
        }
    };

    return (
        <>
            <div className={`fixed inset-0 z-30 bg-black bg-opacity-50 transition-opacity ${sidebarOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'}`} onClick={handleOverlayClick}></div>
            <div className={`fixed top-0 left-0 z-50 h-full bg-white shadow-md rounded-lg p-4 flex flex-col items-center transform ${sidebarOpen ? 'translate-x-0 w-2/5' : '-translate-x-full w-0'} transition-transform lg:relative lg:translate-x-0 lg:w-1/5 lg:top-0 lg:z-30`} onTouchStart={handleTouchStart} onTouchMove={handleTouchMove} onTouchEnd={handleTouchEnd} style={{ userSelect: 'none' }}>
                <ul className="w-full">
                    {renderLinks()}
                </ul>
            </div>
        </>
    );
};

export default Sidebar;
