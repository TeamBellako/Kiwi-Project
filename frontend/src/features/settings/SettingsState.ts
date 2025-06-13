import {SettingsDTO} from "./SettingsDTO";
import {ErrorDetails} from "../../services/network/API";

export enum RetryAction {
    LOAD = 'LOAD',
    UPDATE = 'UPDATE',
}

export type SettingsState = {
    settingsDTO: SettingsDTO | null;
    status: 'idle' | 'loading' | 'succeeded' | 'failed';
    error: ErrorDetails | null;
    retryAction: RetryAction | null;
};

export const initialState: SettingsState = {
    settingsDTO: null,
    status: 'idle',
    error: null,
    retryAction: null,
};