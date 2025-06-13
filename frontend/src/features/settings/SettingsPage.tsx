import React, {useEffect} from 'react';
import {useSettingsForm} from './SettingsHook';
import {SettingsDTO} from './SettingsDTO';
import {selectSettingsDTO, selectSettingsError, selectSettingsStatus} from "./SettingsSelector";
import {loadSettings} from "./SettingsThunks";
import {useAuth} from "../../services/navigation/Authentication";
import {useAppDispatch, useAppSelector} from "../../services/store/Hooks";
import LoadingModal from "../../ui/modals/LoadingModal";
import ErrorModal from "../../ui/modals/ErrorModal";
import {Kiwi_InputField} from '../../ui/components/Kiwi_InputField';
import {TestIDs} from "../../services/common/TestIDs";
import {Kiwi_Slider} from "../../ui/components/Kiwi_Slider";
import {Kiwi_Button} from "../../ui/components/Kiwi_Button";

type SettingsProps = Partial<SettingsDTO>;

const SNAP_VALUES = [0, 33, 67, 100];

const snapToClosest = (value: number) => {
    return SNAP_VALUES.reduce((prev, curr) =>
        Math.abs(curr - value) < Math.abs(prev - value) ? curr : prev
    );
};

const SettingsPage: React.FC<SettingsProps> = (props) => {
    const form = useSettingsForm(props);
    const { logoutUser } = useAuth();

    const dispatch = useAppDispatch();

    const settingsDTO = useAppSelector(selectSettingsDTO);
    const status = useAppSelector(selectSettingsStatus);
    const error = useAppSelector(selectSettingsError);

    useEffect(() => {
        dispatch(loadSettings());
        form.emailField.setValue(settingsDTO?.email!!); // we read from the DTO here because this field is read-only
    }, []);

    if (status === 'loading') {
        return (
            <LoadingModal />
        );
    }

    if (status === 'failed' && error?.code && error.code >= 500) {
        return (
            <ErrorModal
                onRetry={form.handleRetry}
            />
        );
    }

    return (
        <div className="p-4 max-w-lg mx-auto">
            {settingsDTO && (
                <>
                    <form className="space-y-6" data-testid="settings-form">
                        <Kiwi_InputField
                            label="Email"
                            type="email"
                            value={settingsDTO.email} 
                            required={false}
                            testID={TestIDs.settings.email}
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
                            testID={TestIDs.settings.musicVolume}
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
                            testID={TestIDs.settings.soundVolume}
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

export default SettingsPage;
