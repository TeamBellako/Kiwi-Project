import {UserSettings} from "../../userSettings/types/UserSettings";

export const validUserSettings: UserSettings = {
    id: 1,
    email: 'finn@thehuman.com',
    areNotificationsEnabled: true,
    theme: 'DARK',
};

export const updateUserSettings: UserSettings = {
    id: 1,
    email: 'jake@thedog.com',
    areNotificationsEnabled: false,
    theme: 'LIGHT',
};

export const invalidUserSettings: UserSettings = {
    id: -1,
    email: 'bmolovesfootball.com',
    areNotificationsEnabled: true,
    theme: 'DARK',
};