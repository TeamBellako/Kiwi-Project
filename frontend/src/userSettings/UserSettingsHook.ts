import {useCallback, useEffect, useRef, useState} from 'react';
import {debounce, isEqual} from 'lodash';
import {useDispatch} from 'react-redux';
import {AppDispatch} from '../store/Store';
import {Logger} from '../utils/Logger';
import {UserSettingsDTO} from './UserSettingsDTO';
import {Email} from '../users/Email';
import {loadUserSettings, updateUserSettings} from './UserSettingsThunks';
import {RetryAction} from "./UserSettingsState";
import {useAppSelector} from "../store/Hooks";
import {selectUserSettingsRetryAction} from "./UserSettingsSelector";

type UserSettingsFormProps = Partial<UserSettingsDTO>;

export const useUserSettingsForm = ({
    email = '',
    soundVolume = 67,
    musicVolume = 67,
}: UserSettingsFormProps) => {
    const dispatch = useDispatch<AppDispatch>();
    
    const retryAction = useAppSelector(selectUserSettingsRetryAction);

    const [formState, setFormState] = useState<UserSettingsDTO>({
        email,
        soundVolume,
        musicVolume,
    });

    const prevValueRef = useRef<UserSettingsDTO | null>(null);
    const isFirstRender = useRef(true);

    const [error, setError] = useState('');
    const [isSaving, setIsSaving] = useState(false);

    const saveSettings = useCallback(
        debounce(async (updatedSettings: UserSettingsDTO) => {
            try {
                setIsSaving(true);
                Logger.info('Saving settings');

                Email.of(updatedSettings.email); // throws if invalid
                await dispatch(updateUserSettings(updatedSettings));

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

        if (formState.email) {
            try {
                Email.of(formState.email);
                if (error.length > 0) setError('');
            } catch {
                setError('Invalid email format');
                return;
            }
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
            dispatch(loadUserSettings());
        } else if (retryAction === RetryAction.UPDATE) {
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