import {UserSettingsDTO} from "../../userSettings/types/UserSettingsDTO";

export const validUserSettingsDTO: UserSettingsDTO = {
    email: 'finn@thehuman.com',
    areNotificationsEnabled: true,
    theme: 'DARK'
};

export const updateUserSettingsDTO: UserSettingsDTO = {
    email: 'finn@thehuman.com',
    areNotificationsEnabled: false,
    theme: 'LIGHT'
};

export const invalidUserSettingsDTO: UserSettingsDTO = {
    email: 'bmolovesfootball.com',
    areNotificationsEnabled: true,
    theme: 'DARK'
};