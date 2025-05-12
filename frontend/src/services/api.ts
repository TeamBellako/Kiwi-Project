import axios from 'axios';

// TODO: Remove when accounts are implemented
const jwtToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmaW5uQHRoZWh1bWFuLmNvbSIsImlhdCI6MTc0NzAzNzc3OSwiZXhwIjoxNzQ3MDQxMzc5fQ.Osx-F9-dtbezirNPW0PogiI1pZs8ERMXL9UFJFYEQHA";

const api = axios.create({
    baseURL: import.meta.env.VITE_FRONT_API_URL,
    headers: {
        Authorization: jwtToken,
    },
});

export default api;