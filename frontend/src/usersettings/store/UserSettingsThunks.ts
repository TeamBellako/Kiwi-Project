import { createAsyncThunk } from '@reduxjs/toolkit';
import api from "../../services/api";
import { pingServer } from "../../services/pingServer";
import {
    UserSettings,
    UserSettingsDTO,
    toDomainObject,
    toDTO
} from "../types/UserSettingsTypes";

export const loadUserSettings = createAsyncThunk<UserSettings>(
    'userSettings/loadUserSettings',
    async (_, { rejectWithValue }) => {
        try {
            const response = await api.get<UserSettingsDTO>('/api/settings/me');
            return toDomainObject(response.data);
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
            // TODO: using id = 1 since JWT isn't in place yet
            const dto: UserSettingsDTO = toDTO(settings, 1);
            const response = await api.put<UserSettingsDTO>('/api/settings', dto);
            return toDomainObject(response.data);
        } catch (error: any) {
            const status = error.response?.status;

            const message = status === 500
                ? 'Internal server error. Try again later.'
                : error.response?.data?.message || 'Failed to update user settings';

            return rejectWithValue(message);
        }
    }
);
