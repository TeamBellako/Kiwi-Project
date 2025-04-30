import {createAsyncThunk} from '@reduxjs/toolkit';
import {UserSettings} from "../types/UserSettings";
import api from "../../services/api";

export const loadUserSettings = createAsyncThunk<UserSettings>(
    'userSettings/loadUserSettings',
    async (_, { rejectWithValue }) => {
        try {
            const response = await api.get('/api/settings/me')
            return response.data;
        } catch (error: any) {
            return rejectWithValue('Failed to load user settings');
        }
    }
);

export const updateUserSettings = createAsyncThunk<UserSettings, UserSettings>(
    'userSettings/saveUserSettings',
    async (settings, { rejectWithValue }) => {
        try {
            const response = await api.put('/api/settings', settings);
            return response.data;
        } catch (error: any) {
            return rejectWithValue(error.message || 'Failed to update user settings');
        }
    }
);
