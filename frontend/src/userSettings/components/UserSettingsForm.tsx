import React from 'react';
import {useUserSettingsForm} from '../hooks/UserSettingsHook';
import {UserSettingsDTO} from '../types/UserSettingsDTO';
import {useAuth} from "../../navigation/Logout";
import {Kiwi_InputField} from "../../ui/components/Kiwi_InputField";
import {Kiwi_Button} from "../../ui/components/Kiwi_Button";

type UserSettingsProps = Partial<UserSettingsDTO>;

const UserSettingsForm: React.FC<UserSettingsProps> = (props) => {
    const form = useUserSettingsForm(props);
    const { logoutUser } = useAuth();

    return (
        <>
            <form className="space-y-6" data-testid="settings-form">
                <Kiwi_InputField
                    type="email"
                    value={form.emailField.value}
                    required={false}
                />
                
                <label htmlFor="notifications" className="flex items-center gap-2">
                    <input
                        id="notifications"
                        type="checkbox"
                        checked={form.notificationsField.enabled}
                        onChange={form.notificationsField.onToggle}
                    />
                    Enable Notifications
                </label>

                <fieldset>
                    <legend className="block font-medium mb-1">Theme</legend>
                    <label className="flex items-center gap-2">
                        <input
                            type="radio"
                            name="theme"
                            value="LIGHT"
                            checked={form.themeField.value.toUpperCase() === 'LIGHT'}
                            onChange={() => form.themeField.setValue('LIGHT')}
                        />
                        Light
                    </label>
                    <label className="flex items-center gap-2">
                        <input
                            type="radio"
                            name="theme"
                            value="DARK"
                            checked={form.themeField.value.toUpperCase() === 'DARK'}
                            onChange={() => form.themeField.setValue('DARK')}
                        />
                        Dark
                    </label>
                </fieldset>
            </form>
            
            <div className="mt-6">
                <Kiwi_Button 
                    text="Logout"
                    onClick={logoutUser}
                    disabled={false}
                />
            </div>
        </>
    );
};

export default UserSettingsForm;
