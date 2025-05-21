import {createAsyncThunk} from "@reduxjs/toolkit";
import {UsersDTO} from "./UsersDTO";
import api from "../services/api";

export const signup = createAsyncThunk<
    void,             
    UsersDTO,         
    { rejectValue: string }
>(
    'users/signup',
    async (userData, { rejectWithValue }) => {
        try {
            await api.post('api/public/signup', userData);
        } catch (error: any) {
            const status = error.response?.status;
            
            const message =
                status === 500
                    ? 'Internal server error. Try again later.'
                    : error.response?.data?.error || 'Failed to sign up';

            return rejectWithValue(message);
        }
    }
);

export const login = createAsyncThunk<
    string,           
    UsersDTO,         
    { rejectValue: string }
>(
    'users/login',
    async (userData, { rejectWithValue }) => {
        try {
            const response = await api.post<{ token: string }>('api/public/login', userData);
            return response.data.token; 
        } catch (error: any) {
            const status = error.response?.status;
            
            const message =
                status === 500
                    ? 'Internal server error. Try again later.'
                    : error.response?.data?.error || 'Failed to log in';

            return rejectWithValue(message);
        }
    }
);
