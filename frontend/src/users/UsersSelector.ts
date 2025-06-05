import {RootState} from "../store/Store";

export const selectUsersDTO = (state: RootState) => state.users.usersDTO
export const selectUsersStatus = (state: RootState) => state.users.status;
export const selectUsersError = (state: RootState) => state.users.error;
export const selectUsersRetryAction = (state: RootState) => state.users.retryAction;