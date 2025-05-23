import {combineReducers, configureStore} from '@reduxjs/toolkit';
import {usersReducer} from "../users/UsersSlice";
import {userSettingsReducer} from "../userSettings/UserSettingsSlice";

const rootReducer = combineReducers({
    userSettings: userSettingsReducer,
    users: usersReducer,
});
export const store = configureStore({
    reducer: rootReducer,
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
