import {createAsyncThunk} from "@reduxjs/toolkit";
import {UsersDTO} from "./UsersDTO";
import API, {ErrorDetails} from "../../services/network/API";
import {getServerErrorMessage} from "../../services/common/HTTPUtils";

export const signup = createAsyncThunk<
    void,             
    UsersDTO,        
    { rejectValue: ErrorDetails } 
>(
    'users/signup',
    async (userData, { rejectWithValue }) => {
        try {
            await API.post('api/public/signup', userData);
        } catch (error: any) {
            return rejectWithValue(getServerErrorMessage(error, 'Failed to signup'));
        }
    }
);

export const login = createAsyncThunk<
    string,           
    UsersDTO,        
    { rejectValue: ErrorDetails } 
>(
    'users/login',
    async (userData, { rejectWithValue }) => {
        try {
            const response = await API.post<{ jwt: string }>('api/public/login', userData);
            return response.data.jwt; 
        } catch (error: any) {
            return rejectWithValue(getServerErrorMessage(error, 'Failed to login'));
        }
    }
);
