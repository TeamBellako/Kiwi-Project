import React from 'react';
import {useUserSettingsForm} from '../hooks/UserSettingsHook';
import {UserSettingsDTO} from '../types/UserSettingsDTO';
import {useAuth} from '../../navigation/Logout';
import {Kiwi_InputField} from '../../ui/components/Kiwi_InputField';
import {Kiwi_Button} from '../../ui/components/Kiwi_Button';

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

                <div className="flex flex-col gap-2">
                    <label htmlFor="soundVolume" className="font-medium">
                        Sound Volume
                    </label>
                    <input
                        id="soundVolume"
                        type="range"
                        min={0}
                        max={100}
                        value={form.soundVolumeField.value}
                        onChange={(e) => form.soundVolumeField.setValue(Number(e.target.value))}
                        className="w-full"
                    />
                </div>

                <div className="flex flex-col gap-2">
                    <label htmlFor="musicVolume" className="font-medium">
                        Music Volume
                    </label>
                    <input
                        id="musicVolume"
                        type="range"
                        min={0}
                        max={100}
                        value={form.musicVolumeField.value}
                        onChange={(e) => form.musicVolumeField.setValue(Number(e.target.value))}
                        className="w-full"
                    />
                </div>
            </form>

            <div className="mt-6">
                <Kiwi_Button text="Logout" onClick={logoutUser} disabled={false} />
            </div>
        </>
    );
};

export default UserSettingsForm;
