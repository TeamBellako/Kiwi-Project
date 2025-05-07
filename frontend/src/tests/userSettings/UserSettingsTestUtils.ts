import {screen} from "@testing-library/react";
import {userSettingsLabels} from "../constants/Labels";

export const getFormFields = () => {
    const getEmailInput = () =>
        screen.getByRole('textbox', { name: userSettingsLabels.email });
    const getNotificationToggle = () =>
        screen.getByRole('checkbox', { name: userSettingsLabels.notifications });
    const getThemeRadio = (theme: 'light' | 'dark') =>
        screen.getByRole('radio', { name: new RegExp(theme, 'i') });

    return {
        getEmailInput,
        getNotificationToggle,
        getThemeRadio
    };
};