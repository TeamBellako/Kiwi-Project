import { createAsyncThunk, createSlice, PayloadAction } from "@reduxjs/toolkit";
import api from "../services/api";

export interface HelloState {
    value: string;
    status: 'idle' | 'loading' | 'succeeded' |  'failed';
};

const initialState: HelloState = {
    value: "",
    status: 'idle'
}

export const fetchHelloMessage = createAsyncThunk<string, number>(
    "hello/fetchMessage",
    async (id, { rejectWithValue }) => {
        try {
            const response = await api.get(`api/hello/${id}`);
            return response.data.message;
        } catch (error) {
            return rejectWithValue(error);
        }
    }
);

const helloSlice = createSlice({
    name: "hello",
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder
            .addCase(fetchHelloMessage.pending, (state) => {
                state.status = "loading";
            })
            .addCase(
                fetchHelloMessage.fulfilled,
                (state, action: PayloadAction<string>) => {
                    state.status = "succeeded";
                    state.value = action.payload;
                }
            )
            .addCase(fetchHelloMessage.rejected, (state) => {
                state.status = "failed";
            });
    },
});

export default helloSlice.reducer;