import {createAsyncThunk} from '@reduxjs/toolkit';
import {UserSettings} from "../types/UserSettings";
import api from "../../services/api";

export const loadUserSettings = createAsyncThunk<UserSettings>(
    'userSettings/loadUserSettings',
    async (_, { rejectWithValue }) => {
        try {
            const response = await api.get('/api/settings/me');
            return response.data;
        } catch (error: any) {
            const status = error.response?.status;
            const message = status === 500
                ? 'Internal server error. Try again later.'
                : error.response?.data?.message || 'Failed to load user settings';
            return rejectWithValue(message);
        }
    }
);

export const updateUserSettings = createAsyncThunk<UserSettings, UserSettings>(
    'userSettings/updateUserSettings',
    async (settings, { rejectWithValue }) => {
        try {
            const response = await api.put('/api/settings', settings);
            return response.data;
        } catch (error: any) {
            const status = error.response?.status;
            const message = status === 500
                ? 'Internal server error. Try again later.'
                : error.response?.data?.message || 'Failed to update user settings';
            return rejectWithValue(message);
        }
    }
);
