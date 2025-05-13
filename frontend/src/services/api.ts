import axios from 'axios';

// TODO: Remove when accounts are implemented
const jwtToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmaW5uQHRoZWh1bWFuLmNvbSIsImlhdCI6MTc0NzEyNzE3MSwiZXhwIjoxNzQ3MTMwNzcxfQ.qp1KggTrBdEwRqPfeN6LfBKYfSLAvKsEfp5mMYtUPHs";

const api = axios.create({
    baseURL: import.meta.env.VITE_FRONT_API_URL,
    headers: {
        Authorization: jwtToken,
    },
});

export default api;