import {createAsyncThunk, createSlice} from '@reduxjs/toolkit';
import {UserSettings} from "./UserSettings";

// Mock API calls — replace with real API later
const mockFetchUserSettings = async (): Promise<UserSettings> => {
    return {
        email: 'finn@thehuman.com',
        areNotificationsEnabled: true,
        theme: 'dark',
    };
};

const mockUpdateUserSettings = async (settings: UserSettings): Promise<UserSettings> => {
    return settings; // Echo back
};

// === Thunks ===
export const fetchUserSettings = createAsyncThunk<UserSettings>(
    'userSettings/fetchUserSettings',
    async () => {
        return await mockFetchUserSettings();
    }
);

export const updateUserSettings = createAsyncThunk<UserSettings, UserSettings>(
    'userSettings/updateUserSettings',
    async (settings) => {
        return await mockUpdateUserSettings(settings);
    }
);

// === State Type ===
export type UserSettingsState = {
    userSettings: UserSettings | null;
    status: 'idle' | 'loading' | 'succeeded' | 'failed';
    error: string | null;
};

// === Initial State ===
export const initialState: UserSettingsState = {
    userSettings: null,
    status: 'idle',
    error: null,
};

// === Slice ===
const userSettingsSlice = createSlice({
    name: 'userSettings',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder
            // === fetchUserSettings
            .addCase(fetchUserSettings.pending, (state) => {
                state.status = 'loading';
                state.error = null;
            })
            .addCase(fetchUserSettings.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.userSettings = action.payload;
                state.error = null;
            })
            .addCase(fetchUserSettings.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.error.message ?? 'Failed to fetch user settings';
            })

            // === updateUserSettings
            .addCase(updateUserSettings.pending, (state) => {
                state.status = 'loading';
            })
            .addCase(updateUserSettings.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.userSettings = action.payload;
            })
            .addCase(updateUserSettings.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.error.message ?? 'Failed to update user settings';
            });
    },
});

export const userSettingsReducer = userSettingsSlice.reducer;
