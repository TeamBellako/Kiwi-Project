import axios from 'axios';

// TODO: Remove when accounts are implemented
const jwtToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmaW5uQHRoZWh1bWFuLmNvbSIsInJvbGVzIjpbIlVTRVIiXSwiaWF0IjoxNzQ3MTMxODc5LCJleHAiOjE3NDcxMzU0Nzl9.3Wz73Chmhib46l7HPDKW6WGx1Jy2Oy4PwK13BeuTxyg";

const api = axios.create({
    baseURL: import.meta.env.VITE_FRONT_API_URL,
    headers: {
        Authorization: jwtToken,
    },
});

export default api;