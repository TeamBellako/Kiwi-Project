import {useCallback, useEffect, useRef, useState} from "react";
import {isValidEmail} from "../../utils/ValidationUtils";
import {debounce, isEqual} from "lodash";
import {useDispatch} from "react-redux";
import {updateUserSettings} from "../store/UserSettingsThunks";
import {AppDispatch} from "../../store/Store";
import {Logger} from "../../utils/Logger";
import {ThemeOption, UserSettings} from "../types/UserSettingsTypes";

type UserSettingsFormProps = Partial<UserSettings>;
export const useUserSettingsForm = ({
    email = '',
    areNotificationsEnabled = false,
    theme = 'LIGHT',
}: UserSettingsFormProps) => {
    const dispatch = useDispatch<AppDispatch>();

    const [formState, setFormState] = useState<UserSettings>({
        email,
        areNotificationsEnabled,
        theme,
    });

    const prevValueRef = useRef<UserSettings | null>(null);
    const isFirstRender = useRef(true);

    const [error, setError] = useState('');
    const [isSaving, setIsSaving] = useState(false);

    const saveSettings = useCallback(
        debounce(async (updatedSettings: UserSettings) => {
            try {
                setIsSaving(true);
                Logger.info("Saving settings");
                await dispatch(updateUserSettings(updatedSettings));
                setIsSaving(false);
            } catch (err) {
                Logger.error("Failed to save settings", err);
                setIsSaving(false);
            }
        }, 500),
        [dispatch]
    );

    useEffect(() => {
        if (isFirstRender.current) {
            isFirstRender.current = false;
            prevValueRef.current = formState;
            return;
        }

        if (formState.email && !isValidEmail(formState.email)) {
            setError('Invalid email format');
            return;
        } else if (error.length > 0) {
            setError('');
        }

        if (!isEqual(prevValueRef.current, formState)) {
            Logger.info("Queuing save settings");
            prevValueRef.current = formState;
            saveSettings(formState);
        }
    }, [formState, error, saveSettings]);

    return {
        emailField: {
            value: formState.email,
            setValue: (email: string) => setFormState({ ...formState, email }),
            error,
            setError,
        },
        notificationsField: {
            enabled: formState.areNotificationsEnabled,
            onToggle: () =>
                setFormState((prev) => ({
                    ...prev,
                    areNotificationsEnabled: !prev.areNotificationsEnabled,
                })),
        },
        themeField: {
            value: formState.theme,
            setValue: (theme: ThemeOption) => setFormState({ ...formState, theme }),
        },
        isSaving,
    };
};