import {SettingsDTO} from "./SettingsDTO";
import {Email} from "../users/Email";

export interface Settings {
    email: Email;
    soundVolume: number;
    musicVolume: number;
}

export const toDTO = (settings: Settings): SettingsDTO => ({
    email: settings.email.value,
    soundVolume: settings.soundVolume,
    musicVolume: settings.musicVolume,
});