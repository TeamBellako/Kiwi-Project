import {createAsyncThunk} from '@reduxjs/toolkit';
import api from '../../services/api';
import {pingServer} from '../../services/pingServer';
import {UserSettingsDTO} from "../types/UserSettingsDTO";

type RejectMessage = string;


export const loadUserSettings = createAsyncThunk<
    UserSettingsDTO,
    void,
    { rejectValue: RejectMessage }
>(
    'userSettings/loadUserSettings',
    async (_, { rejectWithValue }) => {
        try {
            const response = await api.get<UserSettingsDTO>('/api/settings');
            return response.data;
        } catch (error: any) {
            const status = error.response?.status;

            const message =
                status === 500
                    ? 'Internal server error. Try again later.'
                    : error.response?.data?.message || 'Failed to load user settings';

            return rejectWithValue(message);
        }
    }
);

export const updateUserSettings = createAsyncThunk<
    UserSettingsDTO,
    UserSettingsDTO,
    { rejectValue: RejectMessage }
>(
    'userSettings/updateUserSettingsDTO',
    async (settingsDTO, { rejectWithValue }) => {
        const isServerAlive = await pingServer();
        if (!isServerAlive) {
            return rejectWithValue('Server is not reachable.');
        }

        try {
            const response = await api.put<UserSettingsDTO>('/api/settings', settingsDTO);
            return response.data;
        } catch (error: any) {
            const status = error.response?.status;

            const message =
                status === 500
                    ? 'Internal server error. Try again later.'
                    : error.response?.data?.message || 'Failed to update user settings';

            return rejectWithValue(message);
        }
    }
);
