import {UserSettings} from "../../userSettings/types/UserSettingsTypes";

export const validUserSettings: UserSettings = {
    email: 'finn@thehuman.com',
    areNotificationsEnabled: true,
    theme: 'DARK',
};

export const updateUserSettings: UserSettings = {
    email: 'jake@thedog.com',
    areNotificationsEnabled: false,
    theme: 'LIGHT',
};

export const invalidUserSettings: UserSettings = {
    email: 'bmolovesfootball.com',
    areNotificationsEnabled: true,
    theme: 'DARK',
};