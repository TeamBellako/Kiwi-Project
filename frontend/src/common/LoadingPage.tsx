import React from "react";

const LoadingPage: React.FC = () => {
    return (
        <div className="flex justify-center items-center flex-col h-screen bg-white">
            <div className="w-12 h-12 border-4 border-t-4 border-gray-200 border-t-blue-500 rounded-full animate-spin" />
        </div>
    );
};

export default LoadingPage;
