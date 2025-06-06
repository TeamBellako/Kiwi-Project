import {RootState} from "../store/Store"

export const selectUserSettingsDTO = (state: RootState) => state.userSettings.userSettingsDTO;
export const selectUserSettingsStatus = (state: RootState) => state.userSettings.status;
export const selectUserSettingsError = (state: RootState) => state.userSettings.error;
export const selectUserSettingsRetryAction = (state: RootState) => state.userSettings.retryAction;