import React from 'react';
import UserSettingsEmailField from './UserSettingsEmailField';
import UserSettingsNotificationsToggle from './UserSettingsNotificationsToggle';
import UserSettingsThemeSelector from './UserSettingsThemeSelector';
import {useUserSettingsForm} from '../hooks/UserSettingsHook';
import {UserSettingsDTO} from '../types/UserSettingsDTO';
import {useAuth} from "../../navigation/Logout";

type UserSettingsProps = Partial<UserSettingsDTO>;

const UserSettingsForm: React.FC<UserSettingsProps> = (props) => {
    const form = useUserSettingsForm(props);
    const { logoutUser } = useAuth();

    return (
        <>
            <form className="space-y-6" data-testid="settings-form">
                <UserSettingsEmailField {...form.emailField} />
                <UserSettingsNotificationsToggle {...form.notificationsField} />
                <UserSettingsThemeSelector {...form.themeField} />
            </form>

            <div className="mt-6">
                <button
                    type="button"
                    onClick={logoutUser}
                    className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700"
                >
                    Logout
                </button>
            </div>
        </>
    );
};

export default UserSettingsForm;
