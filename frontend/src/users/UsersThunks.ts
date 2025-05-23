import {createAsyncThunk} from "@reduxjs/toolkit";
import {UsersDTO} from "./UsersDTO";
import api from "../services/api";
import {getServerErrorMessage} from "../utils/HTTPUtils";

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
            return rejectWithValue(getServerErrorMessage(error, 'Failed to signup'));
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
            const response = await api.post<{ jwt: string }>('api/public/login', userData);
            return response.data.jwt; 
        } catch (error: any) {
            return rejectWithValue(getServerErrorMessage(error, 'Failed to login'));
        }
    }
);