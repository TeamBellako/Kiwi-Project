import {UserSettings} from "../../userSettings/UserSettings";

export const validUserSettings: UserSettings = {
    email: 'finn@thehuman.com',
    areNotificationsEnabled: true,
    theme: 'dark',
};

export const inValidUserSettings: UserSettings = {
    email: 'bmolovesfootball.com',
    areNotificationsEnabled: true,
    theme: 'dark',
};