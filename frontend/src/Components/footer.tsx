import React from 'react';

const Footer: React.FC = () => {
    return (
        <footer className="bg-blue-700 text-white py-4">
            <div className="container mx-auto text-center">
                <p>&copy; 2024 문서 자동화 서비스 ILLITERATE</p>
                <div className="flex justify-center space-x-4 mt-2">
                    <a href="https://github.com/pojun406/illiterate" className="hover:underline">프로젝트 소스</a>
                </div>
            </div>
        </footer>
    );
};

export default Footer;
