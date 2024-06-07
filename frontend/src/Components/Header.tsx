import React, { useRef, useEffect, useState } from "react";
import { Outlet, useLocation } from "react-router-dom";
import Navigation from "./Navigation/Navigation";

const Header = () => {
    const mainRef = useRef<HTMLDivElement>(null);
    const [scrollPosition, setScrollPosition] = useState(0);
    const location = useLocation();
    const isHomePage = location.pathname === "/";

    useEffect(() => {
        const handleScroll = () => {
            if (mainRef.current) {
                setScrollPosition(mainRef.current.scrollTop);
            }
        };

        const mainElement = mainRef.current;
        if (mainElement) {
            mainElement.addEventListener('scroll', handleScroll);
        }

        return () => {
            if (mainElement) {
                mainElement.removeEventListener('scroll', handleScroll);
            }
        };
    }, []);

    return (
        <div className="flex flex-col h-screen">
            <header className={`fixed top-0 left-0 w-full z-50 shadow-md lg:px-8 ${isHomePage ? 'bg-black text-white' : 'bg-white text-black'}`}>
                <Navigation />
            </header>
            <main ref={mainRef} className="flex-1 overflow-y-scroll mt-20 scrollbar-hide">
                <Outlet context={{ scrollPosition }} />
            </main>
        </div>
    );
};

export default Header;