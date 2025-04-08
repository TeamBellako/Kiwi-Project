import {Action, configureStore, ThunkAction} from '@reduxjs/toolkit';

import helloReducer from './helloSlice';

const store = configureStore({
    reducer: {
        hello: helloReducer,
    },
});

export type AppStore = typeof store
export type RootState = ReturnType<AppStore['getState']>
export type AppDispatch = AppStore['dispatch']
export type AppThunk<ThunkReturnType = void> = ThunkAction<
    ThunkReturnType,
    RootState,
    unknown,
    Action
>
export default store;
