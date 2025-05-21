import {combineReducers, configureStore} from '@reduxjs/toolkit';
import {userSettingsReducer} from "../userSettings/store/UserSettingsSlice";
import {usersReducer} from "../users/UsersSlice";

const rootReducer = combineReducers({
    userSettings: userSettingsReducer,
    users: usersReducer,
});
export const store = configureStore({
    reducer: rootReducer,
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
