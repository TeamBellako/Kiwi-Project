import {createSlice} from '@reduxjs/toolkit';
import {loadUserSettings, updateUserSettings} from './UserSettingsThunks';
import {initialState, RetryAction} from "./UserSettingsState";
import {ErrorDetails} from "../services/api";

const userSettingsSlice = createSlice({
    name: 'userSettings',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder
            .addCase(loadUserSettings.pending, (state) => {
                state.status = 'loading';
                state.error = null;
                state.retryAction = null;
            })
            .addCase(loadUserSettings.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.userSettingsDTO = action.payload;
                state.error = null;
                state.retryAction = null;
            })
            .addCase(loadUserSettings.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.payload as ErrorDetails;
                state.retryAction = RetryAction.LOAD;
            })
            
            .addCase(updateUserSettings.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.error = null;
                state.userSettingsDTO = action.payload;
                state.retryAction = null;
            })
            .addCase(updateUserSettings.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.payload as ErrorDetails;
                state.retryAction = null;
                state.retryAction = RetryAction.UPDATE;

            });
    },
});
export const userSettingsReducer = userSettingsSlice.reducer;