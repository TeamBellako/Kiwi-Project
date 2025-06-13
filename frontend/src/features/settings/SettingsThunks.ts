import {createAsyncThunk} from '@reduxjs/toolkit';
import {SettingsDTO} from "./SettingsDTO";
import API, {ErrorDetails, pingServer} from "../../services/network/API";
import {getServerErrorMessage} from "../../services/common/HTTPUtils";

export const loadSettings = createAsyncThunk<
    SettingsDTO,
    void,
    { rejectValue: ErrorDetails }
>(
    'settings/loadSettings',
    async (_, { rejectWithValue }) => {
        try {
            const response = await API.get<SettingsDTO>('/api/user/settings');
            return response.data;
        } catch (error: any) {
            return rejectWithValue(getServerErrorMessage(error, 'Failed to load user settings'));
        }
    }
);

export const updateSettings = createAsyncThunk<
    SettingsDTO,
    SettingsDTO,
    { rejectValue: ErrorDetails }
>(
    'settings/updateSettingsDTO',
    async (settingsDTO, { rejectWithValue }) => {
        const isServerAlive = await pingServer();
        if (!isServerAlive) {
            return rejectWithValue(getServerErrorMessage(null, 'Failed to update user settings'));
        }

        try {
            const response = await API.put<SettingsDTO>('/api/user/settings', settingsDTO);
            return response.data;
        } catch (error: any) {
            return rejectWithValue(getServerErrorMessage(error, 'Failed to update user settings'));
        }
    }
);