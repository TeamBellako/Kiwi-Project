import {Email} from "./Email";
import {Password} from "./Password";
import {UsersDTO} from "./UsersDTO";

export interface Users {
    email: Email;
    password: Password;
}

export const toDTO = (user: Users): UsersDTO => ({
    email: user.email.value,
    password: user.password.value
});