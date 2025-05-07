import {createSlice} from '@reduxjs/toolkit';
import {loadUserSettings, updateUserSettings} from './UserSettingsThunks';
import {initialState} from "../types/UserSettingsTypes";

const userSettingsSlice = createSlice({
    name: 'userSettings',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder
            .addCase(loadUserSettings.pending, (state) => {
                state.status = 'loading';
                state.error = null;
            })
            .addCase(loadUserSettings.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.userSettings = action.payload;
            })
            .addCase(loadUserSettings.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.payload as string ?? 'Failed to load user settings';
            })
            
            .addCase(updateUserSettings.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.userSettings = action.payload;
            })
            .addCase(updateUserSettings.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.payload as string ?? 'Failed to update user settings';
            });
    },
});
export const userSettingsReducer = userSettingsSlice.reducer;