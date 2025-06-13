import {createSlice, PayloadAction} from '@reduxjs/toolkit';
import {RetryAction, usersInitialState} from './UsersState';
import {login, signup} from './UsersThunks';
import {tryGetJWTToken} from "../../services/common/StorageUtils";
import {ErrorDetails} from "../../services/network/API";

const usersSlice = createSlice({
    name: 'users',
    initialState: {
        ...usersInitialState,
        token: tryGetJWTToken() || null,
    },
    reducers: {
        logout(state) {
            state.token = null;
            state.usersDTO = null;
            state.retryAction = null;
            state.error = null;
            localStorage.removeItem('jwtToken');
        },
        setError(state, action: PayloadAction<ErrorDetails | null>) {
            state.error = action.payload; 
        },
        setRetryAction(state, action: PayloadAction<RetryAction | null>) {
            state.retryAction = action.payload; 
        },
    },
    extraReducers: (builder) => {
        builder
            .addCase(signup.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.usersDTO = action.payload!!;
                state.retryAction = null;
                state.error = null; 
            })
            .addCase(signup.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.payload as ErrorDetails;
                state.retryAction = RetryAction.SIGNUP;
            })
            
            .addCase(login.pending, (state) => {
                state.status = 'loading';
                state.error = null; 
                state.retryAction = null;
            })
            .addCase(login.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.token = action.payload;
                state.retryAction = null;
                localStorage.setItem('jwtToken', action.payload); 
                state.error = null; 
            })
            .addCase(login.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.payload as ErrorDetails;
                state.token = null;
                state.retryAction = RetryAction.LOGIN;
            })
    },
});

export const { logout, setError, setRetryAction } = usersSlice.actions;
export const usersReducer = usersSlice.reducer;
