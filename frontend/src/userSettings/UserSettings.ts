import {UserSettingsDTO} from "./UserSettingsDTO";
import {Email} from "../users/Email";

export interface UserSettings {
    email: Email;
    soundVolume: number;
    musicVolume: number;
}

export const toDTO = (userSettings: UserSettings): UserSettingsDTO => ({
    email: userSettings.email.value,
    soundVolume: userSettings.soundVolume,
    musicVolume: userSettings.musicVolume,
});