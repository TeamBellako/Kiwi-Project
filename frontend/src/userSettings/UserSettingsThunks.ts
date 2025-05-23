import {createAsyncThunk} from '@reduxjs/toolkit';
import api from '../services/api';
import {pingServer} from '../services/pingServer';
import {UserSettingsDTO} from "./UserSettingsDTO";
import {getServerErrorMessage} from "../utils/HTTPUtils";

type RejectMessage = string;

export const loadUserSettings = createAsyncThunk<
    UserSettingsDTO,
    void,
    { rejectValue: RejectMessage }
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
    { rejectValue: RejectMessage }
>(
    'userSettings/updateUserSettingsDTO',
    async (settingsDTO, { rejectWithValue }) => {
        const isServerAlive = await pingServer();
        if (!isServerAlive) {
            return rejectWithValue('Server is not reachable.');
        }

        try {
            const response = await api.put<UserSettingsDTO>('/api/user/settings', settingsDTO);
            return response.data;
        } catch (error: any) {
            return rejectWithValue(getServerErrorMessage(error, 'Failed to update user settings'));
        }
    }
);