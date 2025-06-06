import React from "react";
import {TestIDs} from "./TestIDs";

interface ErrorPageProps {
    message?: string;
    onRetry?: () => void;
}

const ErrorPage: React.FC<ErrorPageProps> = ({ message = "Something went wrong. Please try again later.", onRetry }) => {
    return (
        <div className="flex flex-col justify-center items-center h-screen bg-white">
            <p className="text-2xl text-red-500 font-semibold mb-2">Oops!</p>
            <p className="text-xl text-red-500 mb-4" data-testid={TestIDs.common.errorPage}>{message}</p>
            {
                onRetry &&
                <button
                    onClick={onRetry}
                    className="py-2 px-4 bg-blue-500 text-white rounded-md hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50"
                >
                    Retry
                </button>
            }
        </div>
    );
};

export default ErrorPage;