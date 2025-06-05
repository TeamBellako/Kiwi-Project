import React from 'react';
import {useUserSettingsForm} from './UserSettingsHook';
import {UserSettingsDTO} from './UserSettingsDTO';
import {Kiwi_InputField} from '../ui/components/Kiwi_InputField';
import {Kiwi_Button} from '../ui/components/Kiwi_Button';
import {useAuth} from '../navigation/Authentication';
import {Kiwi_Slider} from "../ui/components/Kiwi_Slider";

type UserSettingsProps = Partial<UserSettingsDTO>;

const SNAP_VALUES = [0, 33, 67, 100];

const snapToClosest = (value: number) => {
    return SNAP_VALUES.reduce((prev, curr) =>
        Math.abs(curr - value) < Math.abs(prev - value) ? curr : prev
    );
};

const UserSettingsForm: React.FC<UserSettingsProps> = (props) => {
    const form = useUserSettingsForm(props);
    const { logoutUser } = useAuth();

    return (
        <>
            <form className="space-y-6" data-testid="settings-form">
                <Kiwi_InputField
                    label="Email"
                    type="email"
                    value={form.emailField.value}
                    required={false}
                />
                
                <Kiwi_Slider
                    text={"Sound Volume"}
                    label={"soundVolume"}
                    min={0}
                    max={99}
                    step={33}
                    value={form.soundVolumeField.value}
                    onChange={(e) =>
                        form.soundVolumeField.setValue(snapToClosest(Number(e.target.value)))
                    }
                />
                
                <Kiwi_Slider
                    text={"Music Volume"}
                    label={"musicVolume"}
                    min={0}
                    max={99}
                    step={33}
                    value={form.musicVolumeField.value}
                    onChange={(e) =>
                        form.musicVolumeField.setValue(snapToClosest(Number(e.target.value)))
                    }
                />
            </form>

            <div className="mt-6">
                <Kiwi_Button text="Logout" onClick={logoutUser} disabled={false} />
            </div>
        </>
    );
};

export default UserSettingsForm;
