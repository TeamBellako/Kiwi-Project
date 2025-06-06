import {UserSettingsDTO} from "./UserSettingsDTO";
import {ErrorDetails} from "../services/api";

export enum RetryAction {
    LOAD = 'LOAD',
    UPDATE = 'UPDATE',
}

export type UserSettingsState = {
    userSettingsDTO: UserSettingsDTO | null;
    status: 'idle' | 'loading' | 'succeeded' | 'failed';
    error: ErrorDetails | null;
    retryAction: RetryAction | null;
};

export const initialState: UserSettingsState = {
    userSettingsDTO: null,
    status: 'idle',
    error: null,
    retryAction: null,
};