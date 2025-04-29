import React from 'react';
import EmailField from './EmailField';
import NotificationsToggle from './NotificationsToggle';
import ThemeSelector from './ThemeSelector';
import {UserSettings} from "../types/UserSettings";
import {useUserSettingsForm} from "../hooks/UserSettingsHook";

type UserSettingsProps = Partial<UserSettings>;

const UserSettingsForm: React.FC<UserSettingsProps> = (props) => {
    const form = useUserSettingsForm(props);

    return (
        <form className="space-y-6" data-testid="settings-form">
            <EmailField {...form.emailField} />
            <NotificationsToggle {...form.notificationsField} />
            <ThemeSelector {...form.themeField} />
        </form>
    );
};

export default UserSettingsForm;