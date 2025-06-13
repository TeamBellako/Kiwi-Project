import {Users} from "./Users";
import {Email} from "./Email";
import {Password} from "./Password";

export interface UsersDTO {
    email: string;
    password: string;
}

export const toDomainObject = (dto : UsersDTO): Users => ({
    email: Email.of(dto.email),
    password: Password.of(dto.password),
})