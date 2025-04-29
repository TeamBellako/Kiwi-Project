import { createSlice } from '@reduxjs/toolkit';
import {initialState} from "./UserSettingsState";
import {fetchUserSettings, updateUserSettings} from "./UserSettingsThunks";

const userSettingsSlice = createSlice({
    name: 'userSettings',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder
            .addCase(fetchUserSettings.pending, (state) => {
                state.status = 'loading';
                state.error = null;
            })
            .addCase(fetchUserSettings.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.userSettings = action.payload;
            })
            .addCase(fetchUserSettings.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.error.message ?? 'Failed to fetch user settings';
            })

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
