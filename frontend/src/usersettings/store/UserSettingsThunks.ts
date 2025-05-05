import {createAsyncThunk} from '@reduxjs/toolkit';
import {UserSettings} from "../types/UserSettings";
import api from "../../services/api";
import {pingServer} from "../../services/pingServer";

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
    async (settings, { rejectWithValue, dispatch }) => {
        // We check the server here because this ping has a much lower timout, which creates a better UX if the server
        // is down because the user immediately sees an error
        const isServerAlive = await pingServer();
        if (!isServerAlive) {
            await dispatch(loadUserSettings());
            return rejectWithValue('Server is not reachable.');
        }

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
