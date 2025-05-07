import React from 'react';
import UserSettingsEmailField from './UserSettingsEmailField';
import UserSettingsNotificationsToggle from './UserSettingsNotificationsToggle';
import UserSettingsThemeSelector from './UserSettingsThemeSelector';
import {useUserSettingsForm} from "../hooks/UserSettingsHook";
import {UserSettings} from "../types/UserSettingsTypes";

type UserSettingsProps = Partial<UserSettings>;

const UserSettingsForm: React.FC<UserSettingsProps> = (props) => {
    const form = useUserSettingsForm(props);
    
    return (
        <>
            <form className="space-y-6" data-testid="settings-form">
                <UserSettingsEmailField {...form.emailField} />
                <UserSettingsNotificationsToggle {...form.notificationsField} />
                <UserSettingsThemeSelector {...form.themeField} />
            </form>
        </>
    );
};

export default UserSettingsForm;