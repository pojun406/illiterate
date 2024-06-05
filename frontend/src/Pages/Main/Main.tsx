import { useMemo, useState, useEffect, useCallback } from "react";
import { Link } from "react-router-dom";
import useScrollFadeInLeft from "../../hooks/useScrollFadeIn_left";
import useScrollFadeInRight from "../../hooks/useScrollFadeIn_right";

const Main = () => {
    const headerHeight = 80;
    const [windowHeight, setWindowHeight] = useState(window.innerHeight);
    const [windowWidth, setWindowWidth] = useState(window.innerWidth);

    const sections = useMemo(() => [
        { id: "part1", top: 0, bottom: windowHeight * 0.2 - headerHeight },
        { id: "part2", top: windowHeight * 0.2 - headerHeight, bottom: windowHeight * 0.4 - headerHeight },
        { id: "part3", top: windowHeight * 0.4 - headerHeight, bottom: windowHeight * 0.6 - headerHeight },
        { id: "part4", top: windowHeight * 0.6 - headerHeight, bottom: windowHeight * 0.8 - headerHeight },
        { id: "part5", top: windowHeight * 0.8 - headerHeight, bottom: windowHeight - headerHeight }
    ], [headerHeight, windowHeight]);

    const [currentSection, setCurrentSection] = useState(0);
    const [showArrow, setShowArrow] = useState(true);

    const scrollToSection = (index: number) => {
        const element = document.getElementById(sections[index].id);
        if (element) {
            element.scrollIntoView({ behavior: "smooth" });
            setCurrentSection(index);
        }
    };

    const handleNext = () => {
        if (currentSection < sections.length - 1) {
            const nextSectionIndex = currentSection + 1;
            scrollToSection(nextSectionIndex);
        }
    };

    const handleResize = useCallback(() => {
        setWindowHeight(window.innerHeight);
        setWindowWidth(window.innerWidth);
    }, []);

    useEffect(() => {
        window.addEventListener('resize', handleResize);
        return () => {
            window.removeEventListener('resize', handleResize);
        };
    }, [handleResize]);

    useEffect(() => {
        const handleScroll = () => {
            const current = sections.findIndex(section => window.scrollY >= section.top && window.scrollY < section.bottom);
            if (current !== -1 && current !== currentSection) {
                setCurrentSection(current);
            }
            if (window.scrollY < sections[sections.length - 1].top || window.scrollY >= sections[sections.length - 1].bottom) {
                setShowArrow(true);
            } else {
                setShowArrow(false);
            }
        };

        window.addEventListener('scroll', handleScroll);
        return () => {
            window.removeEventListener('scroll', handleScroll);
        };
    }, [sections, currentSection]);

    useEffect(() => {
        if (currentSection === sections.length - 1) {
            setShowArrow(false); 
        } else {
            setShowArrow(true); 
        }
    }, [currentSection, sections]);

    const fadeInPropsList = sections.map((_, index) => {
        return index % 2 === 0 ? useScrollFadeInLeft : useScrollFadeInRight;
    });

    return (
        <div>
            {sections.map((section, index) => {
                const FadeInComponent = fadeInPropsList[index];
                const fadeInProps = FadeInComponent();
                const sectionStyle = { height: `calc(100vh - ${headerHeight}px)`, width: `${windowWidth}px` };

                return (
                    <div key={section.id} id={section.id} className="section flex bg-black justify-center text-white" style={sectionStyle}>
                        <FadeInSection sectionId={section.id} index={index} fadeInProps={fadeInProps} />
                    </div>
                );
            })}
            {showArrow && (
                <div className="fixed bottom-0 left-0 right-0 flex justify-center p-4">
                    <button onClick={handleNext} className="text-white animate-bounce">
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-10 w-10" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="1" d="M19 9l-7 7-7-7" />
                        </svg>
                    </button>
                </div>
            )}
        </div>
    );
};

const FadeInSection = ({ sectionId, index, fadeInProps }: { sectionId: string, index: number, fadeInProps: any }) => {
    return (
        <div 
            className={`flex items-center justify-center p-8 ${
                sectionId === "part2" ? "lg:mr-[20%] md:flex-row md:justify-center md:space-x-0 flex-col" : 
                sectionId === "part3" ? "lg:ml-[20%] md:flex-row md:justify-center md:space-x-0 flex-col" : 
                sectionId === "part4" ? "lg:mr-[20%] md:flex-row md:justify-center md:space-x-0 flex-col" : 
                "flex-col"
            }`} 
            ref={fadeInProps.ref} 
            style={{ ...fadeInProps.style, minHeight: `calc(100vh - 80px)` }}
        >
            {sectionId === "part1" && (
                <p className="text-4xl font-normal">ILLITERATE에 오신것을 환영합니다.</p>
            )}
            {sectionId === "part2" && (
                <>
                    <img src="/image/paper.png" alt="이미지" className="h-80 w-80 p-8"/>
                    <p className="p-12 text-2xl font-bold">종이문서 귀찮으시잖아요.</p>
                </>
            )}
            {sectionId === "part3" && (
                <>
                    <p className="p-12 text-2xl font-bold">사진만 주시면 됩니다.</p>
                    <img src="/image/ocr.png" alt="이미지" className="h-80 w-80 p-8"/>
                </>
            )}
            {sectionId === "part4" && (
                <>
                    <img src="/image/DB.png" alt="이미지" className="h-80 w-80 p-8"/>
                    <div className="p-12 text-2xl font-bold">
                        <p>간편하게 찍어서</p>
                        <p>전자문서로 저장하세요.</p>
                    </div>
                </>
            )}
            {sectionId === "part5" && (
                <>
                    <Link to="/application" className="opacity-1 transition-opacity text-3xl font-bold">시작하기</Link>
                </>
            )}
        </div>
    );
};

export default Main;