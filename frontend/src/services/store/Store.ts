import {combineReducers, configureStore} from '@reduxjs/toolkit';
import {settingsReducer} from "../../features/settings/SettingsSlice";
import {usersReducer} from "../../features/users/UsersSlice";

const rootReducer = combineReducers({
    settings: settingsReducer,
    users: usersReducer,
});
export const store = configureStore({
    reducer: rootReducer,
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
