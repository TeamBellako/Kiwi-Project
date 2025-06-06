import {createAsyncThunk} from '@reduxjs/toolkit';
import api, {ErrorDetails} from '../services/api';
import {pingServer} from '../services/pingServer';
import {UserSettingsDTO} from "./UserSettingsDTO";
import {getServerErrorMessage} from "../utils/HTTPUtils";

export const loadUserSettings = createAsyncThunk<
    UserSettingsDTO,
    void,
    { rejectValue: ErrorDetails }
>(
    'userSettings/loadUserSettings',
    async (_, { rejectWithValue }) => {
        try {
            const response = await api.get<UserSettingsDTO>('/api/user/settings');
            return response.data;
        } catch (error: any) {
            return rejectWithValue(getServerErrorMessage(error, 'Failed to load user settings'));
        }
    }
);

export const updateUserSettings = createAsyncThunk<
    UserSettingsDTO,
    UserSettingsDTO,
    { rejectValue: ErrorDetails }
>(
    'userSettings/updateUserSettingsDTO',
    async (settingsDTO, { rejectWithValue }) => {
        const isServerAlive = await pingServer();
        if (!isServerAlive) {
            return rejectWithValue(getServerErrorMessage(null, 'Failed to update user settings'));
        }

        try {
            const response = await api.put<UserSettingsDTO>('/api/user/settings', settingsDTO);
            return response.data;
        } catch (error: any) {
            return rejectWithValue(getServerErrorMessage(error, 'Failed to update user settings'));
        }
    }
);