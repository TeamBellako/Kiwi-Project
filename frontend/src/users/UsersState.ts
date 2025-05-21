import {UsersDTO} from "./UsersDTO";

export interface UsersState {
    usersDTO: UsersDTO | null;
    status: 'idle' | 'loading' | 'succeeded' | 'failed';
    error: string | null; 
}