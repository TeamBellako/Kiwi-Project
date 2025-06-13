import {UsersDTO} from "./UsersDTO";
import {ErrorDetails} from "../../services/network/API";

export enum RetryAction {
    LOGIN = 'LOGIN',
    SIGNUP = 'SIGNUP',
}

export interface UsersState {
    usersDTO: UsersDTO | null;
    status: 'idle' | 'loading' | 'succeeded' | 'failed';
    error: ErrorDetails | null;
    retryAction: RetryAction | null;
}

export const usersInitialState : UsersState = {
    usersDTO: null,
    status: 'idle',
    error: null,
    retryAction: null,
};