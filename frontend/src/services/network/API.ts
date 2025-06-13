import axios from 'axios';
import {tryGetJWTToken} from "../common/StorageUtils";

export interface ErrorDetails {
    message: string;
    code?: number;
}

const API = axios.create({
    baseURL: import.meta.env.VITE_FRONT_API_URL,
});

API.interceptors.request.use(config => {
    const token = tryGetJWTToken();

    if (token) {
        config.headers = config.headers || {};
        config.headers.Authorization = `Bearer ${token}`;
    } else {
        if (config.headers) {
            delete config.headers.Authorization;
        }
    }

    return config;
}, error => Promise.reject(error));

export const pingServer = async (): Promise<boolean> => {
    const controller = new AbortController();

    const timeout = setTimeout(() => controller.abort(), 250);

    try {
        await API.get('/api/public/ping', { signal: controller.signal });
        return true;
    } catch (error) {
        return false;
    } finally {
        clearTimeout(timeout);
    }
};

export default API;
