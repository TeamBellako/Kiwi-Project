import {UserSettingsDTO} from "./UserSettingsDTO";
import {Email} from "../../users/Email";

export const THEMES = ['LIGHT', 'DARK'] as const;
export type ThemeOption = (typeof THEMES)[number];

export interface UserSettings {
    email: Email;
    areNotificationsEnabled: boolean;
    theme: ThemeOption;
}

export const toDTO = (userSettings: UserSettings): UserSettingsDTO => ({
    email: userSettings.email.value,
    areNotificationsEnabled: userSettings.areNotificationsEnabled,
    theme: userSettings.theme,
});