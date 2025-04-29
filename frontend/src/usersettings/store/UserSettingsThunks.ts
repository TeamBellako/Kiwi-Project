import { createAsyncThunk } from '@reduxjs/toolkit';
import {UserSettings} from "../types/UserSettings";

// TODO: Placeholder until API connection is implemented
const mockFetch = async (): Promise<UserSettings> => ({
    email: 'finn@thehuman.com',
    areNotificationsEnabled: true,
    theme: 'dark',
});

const mockSave = async (settings: UserSettings): Promise<UserSettings> => settings;

export const fetchUserSettings = createAsyncThunk<UserSettings>(
    'userSettings/loadUserSettings',
    async () => {
        return await mockFetch();
    }
);

export const updateUserSettings = createAsyncThunk<UserSettings, UserSettings>(
    'userSettings/saveUserSettings',
    async (settings) => {
        return await mockSave(settings);
    }
);
