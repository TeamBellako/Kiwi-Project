import {createAsyncThunk} from "@reduxjs/toolkit";
import {UsersDTO} from "./UsersDTO";
import api from "../services/api";

export const signup = createAsyncThunk<
    UsersDTO,
    void,
    { rejectValue: string }
>(
    'users/signup',
    async (_, { rejectWithValue }) => {
        try {
            const response = await api.get<UsersDTO>('api/public/signup');
            return response.data;
        } catch (error: any) {
            const status = error.response?.status;

            const message =
                status === 500
                    ? 'Internal server error. Try again later.'
                    : error.response?.data?.message || 'Failed to sign up';

            return rejectWithValue(message);
        }
    }
);

export const login = createAsyncThunk<
    UsersDTO,
    string,
    { rejectValue: string }
>(
    'users/login',
    async (_, { rejectWithValue }) => {
        try {
            const response = await api.get<UsersDTO>('api/public/login');
            return response.data;
        } catch (error: any) {
            const status = error.response?.status;

            const message =
                status === 500
                    ? 'Internal server error. Try again later.'
                    : error.response?.data?.message || 'Failed to sign up';

            return rejectWithValue(message);
        }
    }
);