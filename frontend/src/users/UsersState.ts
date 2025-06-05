import {UsersDTO} from "./UsersDTO";

export enum RetryAction {
    LOGIN = 'LOGIN',
    SIGNUP = 'SIGNUP',
}

export interface UsersState {
    usersDTO: UsersDTO | null;
    status: 'idle' | 'loading' | 'succeeded' | 'failed';
    error: string | null;
    retryAction: RetryAction | null;
}

export const usersInitialState : UsersState = {
    usersDTO: null,
    status: 'idle',
    error: null,
    retryAction: null,
};