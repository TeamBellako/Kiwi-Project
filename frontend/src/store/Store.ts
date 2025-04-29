import {combineReducers, configureStore} from '@reduxjs/toolkit';
import {userSettingsReducer} from "../usersettings/store/UserSettingsSlice";

const rootReducer = combineReducers({
    userSettings: userSettingsReducer,
});
export const store = configureStore({
    reducer: rootReducer,
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
