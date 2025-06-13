import {Settings} from "./Settings";
import {Email} from "../users/Email";

export interface SettingsDTO {
    email: string;
    soundVolume: number;
    musicVolume: number;
}

export const toDomainObject = (dto: SettingsDTO): Settings => ({
    email: Email.of(dto.email),
    soundVolume: dto.soundVolume,
    musicVolume: dto.musicVolume
});