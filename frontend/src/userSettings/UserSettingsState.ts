import {UserSettingsDTO} from "./UserSettingsDTO";

export type UserSettingsState = {
    userSettingsDTO: UserSettingsDTO | null;
    status: 'idle' | 'loading' | 'succeeded' | 'failed';
    error: string | null;
};

export const initialState: UserSettingsState = {
    userSettingsDTO: null,
    status: 'idle',
    error: null,
};