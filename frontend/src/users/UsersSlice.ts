import {createSlice} from '@reduxjs/toolkit';
import {RetryAction, usersInitialState} from './UsersState';
import {login, signup} from './UsersThunks';
import {tryGetJWTToken} from '../utils/StorageUtils';

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
            localStorage.removeItem('jwtToken');
        },
    },
    extraReducers: (builder) => {
        builder
            .addCase(signup.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.usersDTO = action.payload!!;
                state.retryAction = null;
            })
            .addCase(signup.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.payload as string;
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
            })
            .addCase(login.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.payload as string;
                state.token = null;
                state.retryAction = RetryAction.LOGIN;
                localStorage.removeItem('jwtToken');
            });
    },
});

export const { logout } = usersSlice.actions;
export const usersReducer = usersSlice.reducer;
