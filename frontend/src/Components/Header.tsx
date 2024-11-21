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
        <div className="flex flex-col">
            <header className={`w-full z-30 ${isHomePage ? 'bg-black text-white' : 'bg-white text-black'}`}>
                <Navigation />
                {!isHomePage && <hr/>}
            </header>
            <main ref={mainRef} className={`overflow-y-scroll scrollbar-hide`}>
                <Outlet context={{ scrollPosition }} />
            </main>
        </div>
    );
};

export default Header;