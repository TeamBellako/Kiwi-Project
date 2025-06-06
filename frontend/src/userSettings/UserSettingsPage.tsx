import React, {useEffect} from 'react';
import {useUserSettingsForm} from './UserSettingsHook';
import {UserSettingsDTO} from './UserSettingsDTO';
import {Kiwi_InputField} from '../ui/components/Kiwi_InputField';
import {Kiwi_Button} from '../ui/components/Kiwi_Button';
import {useAuth} from '../navigation/Authentication';
import {Kiwi_Slider} from "../ui/components/Kiwi_Slider";
import {useAppDispatch, useAppSelector} from "../store/Hooks";
import {selectUserSettingsDTO, selectUserSettingsError, selectUserSettingsStatus} from "./UserSettingsSelector";
import {loadUserSettings} from "./UserSettingsThunks";
import LoadingPage from "../common/LoadingPage";
import ErrorPage from "../common/ErrorPage";
import {TestIDs} from "../common/TestIDs";

type UserSettingsProps = Partial<UserSettingsDTO>;

const SNAP_VALUES = [0, 33, 67, 100];

const snapToClosest = (value: number) => {
    return SNAP_VALUES.reduce((prev, curr) =>
        Math.abs(curr - value) < Math.abs(prev - value) ? curr : prev
    );
};

const UserSettingsPage: React.FC<UserSettingsProps> = (props) => {
    const form = useUserSettingsForm(props);
    const { logoutUser } = useAuth();

    const dispatch = useAppDispatch();

    const userSettingsDTO = useAppSelector(selectUserSettingsDTO);
    const status = useAppSelector(selectUserSettingsStatus);
    const error = useAppSelector(selectUserSettingsError);

    useEffect(() => {
        dispatch(loadUserSettings());
        form.emailField.setValue(userSettingsDTO?.email!!); // we read from the DTO here because this field is read-only
    }, []);

    if (status === 'loading') {
        return (
            <LoadingPage />
        );
    }

    if (status === 'failed' && error?.code && error.code >= 500) {
        return (
            <ErrorPage
                onRetry={form.handleRetry}
            />
        );
    }

    return (
        <div className="p-4 max-w-lg mx-auto">
            {userSettingsDTO && (
                <>
                    <form className="space-y-6" data-testid="settings-form">
                        <Kiwi_InputField
                            label="Email"
                            type="email"
                            value={userSettingsDTO.email} 
                            required={false}
                            testID={TestIDs.userSettings.email}
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
                            testID={TestIDs.userSettings.musicVolume}
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
                            testID={TestIDs.userSettings.soundVolume}
                        />
                    </form>
                    
                    <div className="mt-6">
                        <Kiwi_Button text="Logout" onClick={logoutUser} disabled={false} />
                    </div>
                </>
            )}
        </div>
    )
};

export default UserSettingsPage;
