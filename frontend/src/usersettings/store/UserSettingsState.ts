import {UserSettings} from "../types/UserSettings";

export type UserSettingsState = {
    userSettings: UserSettings | null;
    status: 'idle' | 'loading' | 'succeeded' | 'failed';
    error: string | null;
};

export const initialState: UserSettingsState = {
    userSettings: null,
    status: 'idle',
    error: null,
};
