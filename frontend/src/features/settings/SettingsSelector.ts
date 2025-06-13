import {RootState} from "../../services/store/Store";

export const selectSettingsDTO = (state: RootState) => state.settings.settingsDTO;
export const selectSettingsStatus = (state: RootState) => state.settings.status;
export const selectSettingsError = (state: RootState) => state.settings.error;
export const selectSettingsRetryAction = (state: RootState) => state.settings.retryAction;