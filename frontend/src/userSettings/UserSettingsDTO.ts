import {UserSettings} from "./UserSettings";
import {Email} from "../users/Email";

export interface UserSettingsDTO {
    email: string;
    soundVolume: number;
    musicVolume: number;
}

export const toDomainObject = (dto: UserSettingsDTO): UserSettings => ({
    email: Email.of(dto.email),
    soundVolume: dto.soundVolume,
    musicVolume: dto.musicVolume
});