import {RootState} from "../../store/Store"

export const selectUserSettings = (state: RootState) => state.userSettings.userSettings;
export const selectUserSettingsStatus = (state: RootState) => state.userSettings.status;
export const selectUserSettingsError = (state: RootState) => state.userSettings.error;