import {createSlice} from '@reduxjs/toolkit';
import {loadSettings, updateSettings} from './SettingsThunks';
import {initialState, RetryAction} from "./SettingsState";
import {ErrorDetails} from "../../services/network/API";

const settingsSlice = createSlice({
    name: 'settings',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder
            .addCase(loadSettings.pending, (state) => {
                state.status = 'loading';
                state.error = null;
                state.retryAction = null;
            })
            .addCase(loadSettings.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.settingsDTO = action.payload;
                state.error = null;
                state.retryAction = null;
            })
            .addCase(loadSettings.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.payload as ErrorDetails;
                state.retryAction = RetryAction.LOAD;
            })
            
            .addCase(updateSettings.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.error = null;
                state.settingsDTO = action.payload;
                state.retryAction = null;
            })
            .addCase(updateSettings.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.payload as ErrorDetails;
                state.retryAction = null;
                state.retryAction = RetryAction.UPDATE;

            });
    },
});
export const settingsReducer = settingsSlice.reducer;