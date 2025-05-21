import {createSlice} from '@reduxjs/toolkit';
import {usersInitialState} from "./UsersState";
import {login, signup} from "./UsersThunks";
import {tryGetJWTToken} from "../utils/StorageUtils";

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
            localStorage.removeItem('jwtToken');
        },
    },
    extraReducers: (builder) => {
        builder
            .addCase(signup.pending, (state) => {
                state.status = 'loading';
                state.error = null;
            })
            .addCase(signup.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.usersDTO = action.payload!!;
            })
            .addCase(signup.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.payload as string;
            })

            .addCase(login.pending, (state) => {
                state.status = 'loading';
                state.error = null;
            })
            .addCase(login.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.token = action.payload;  
                localStorage.setItem('jwtToken', action.payload); 
            })
            .addCase(login.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.payload as string;
                state.token = null;
                localStorage.removeItem('jwtToken');
            });
    },
});

export const { logout } = usersSlice.actions;
export const usersReducer = usersSlice.reducer;
