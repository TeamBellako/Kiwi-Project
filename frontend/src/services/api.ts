import axios from 'axios';
import {tryGetJWTToken} from "../utils/StorageUtils";

const api = axios.create({
    baseURL: import.meta.env.VITE_FRONT_API_URL,
});

api.interceptors.request.use(config => {
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

export default api;
