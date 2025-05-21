import {createSlice} from '@reduxjs/toolkit';
import {usersInitialState} from "./UsersState";
import {login, signup} from "./UsersThunks";

const usersSlice = createSlice({
    name: 'users',
    initialState: usersInitialState,
    reducers: {},
    extraReducers: (builder) => {
        builder
            .addCase(signup.pending, (state) => {
                state.status = 'loading';
                state.error = null;
            })
            .addCase(signup.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.usersDTO = action.payload;
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
                state.usersDTO = action.payload;
            })
            .addCase(login.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.payload as string;
            });
    },
});
export const usersReducer = usersSlice.reducer;