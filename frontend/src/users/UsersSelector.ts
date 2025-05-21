import {RootState} from "../store/Store";

export const selectUserSettingsDTO = (state: RootState) => state.users.usersDTO
export const selectUserSettingsStatus = (state: RootState) => state.users.status;
export const selectUserSettingsError = (state: RootState) => state.users.error;