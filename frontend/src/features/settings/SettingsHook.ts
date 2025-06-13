import {useCallback, useEffect, useRef, useState} from 'react';
import {debounce, isEqual} from 'lodash';
import {useDispatch} from 'react-redux';
import {SettingsDTO} from './SettingsDTO';
import {loadSettings, updateSettings} from './SettingsThunks';
import {RetryAction} from "./SettingsState";
import {selectSettingsRetryAction} from "./SettingsSelector";
import {AppDispatch} from "../../services/store/Store";
import {useAppSelector} from "../../services/store/Hooks";
import {Logger} from "../../services/common/Logger";

type SettingsFormProps = Partial<SettingsDTO>;

export const useSettingsForm = ({
    email = '',
    soundVolume = 67,
    musicVolume = 67,
}: SettingsFormProps) => {
    const dispatch = useDispatch<AppDispatch>();
    
    const retryAction = useAppSelector(selectSettingsRetryAction);

    const [formState, setFormState] = useState<SettingsDTO>({
        email,
        soundVolume,
        musicVolume,
    });

    const prevValueRef = useRef<SettingsDTO | null>(null);
    const isFirstRender = useRef(true);

    const [error, setError] = useState('');
    const [isSaving, setIsSaving] = useState(false);

    const saveSettings = useCallback(
        debounce(async (updatedSettings: SettingsDTO) => {
            try {
                setIsSaving(true);
                Logger.info('Saving settings');

                await dispatch(updateSettings(updatedSettings));

                setIsSaving(false);
            } catch (err: any) {
                Logger.error('Failed to save settings', err);
                setError(err.message || 'Failed to save');
                setIsSaving(false);
            }
        }, 500),
        [dispatch]
    );

    const queueSaveSettings = () => {
        if (isFirstRender.current) {
            isFirstRender.current = false;
            prevValueRef.current = formState;
            return;
        }

        if (!isEqual(prevValueRef.current, formState)) {
            Logger.info('Queuing save settings');
            prevValueRef.current = formState;
            saveSettings(formState);
        }
    }

    useEffect(() => {
        queueSaveSettings();
    }, [formState, error, saveSettings]);

    const handleRetry = async () => {
        if (retryAction === RetryAction.LOAD) {
            dispatch(loadSettings());
        } else if (retryAction === RetryAction.UPDATE) {
            prevValueRef.current = null
            queueSaveSettings();
        }
    };

    return {
        emailField: {
            value: formState.email,
            setValue: (email: string) => setFormState({ ...formState, email }),
            error,
            setError,
        },
        soundVolumeField: {
            value: formState.soundVolume,
            setValue: (value: number) =>
                setFormState({ ...formState, soundVolume: value }),
        },
        musicVolumeField: {
            value: formState.musicVolume,
            setValue: (value: number) =>
                setFormState({ ...formState, musicVolume: value }),
        },
        isSaving,
        handleRetry
    };
};