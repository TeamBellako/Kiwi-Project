import {ErrorDetails} from "../services/api";

export const getServerErrorMessage = (error: any, fallbackMessage: string): ErrorDetails => {
    const message = error?.response?.data?.message || fallbackMessage;
    const code = error?.response?.status || 500;

    return {
        message,
        code
    };
};
