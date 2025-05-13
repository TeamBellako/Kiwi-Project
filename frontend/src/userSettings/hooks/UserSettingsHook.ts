import {useCallback, useEffect, useRef, useState} from "react";
import {debounce, isEqual} from "lodash";
import {useDispatch} from "react-redux";
import {AppDispatch} from "../../store/Store";
import {Logger} from "../../utils/Logger";
import {ThemeOption} from "../types/UserSettings";
import {UserSettingsDTO} from "../types/UserSettingsDTO";
import {Email} from "../../users/Email";
import {updateUserSettings} from "../store/UserSettingsThunks";

type UserSettingsFormProps = Partial<UserSettingsDTO>;

export const useUserSettingsForm = ({
    email = '',
    areNotificationsEnabled = false,
    theme = 'LIGHT',
}: UserSettingsFormProps) => {
    const dispatch = useDispatch<AppDispatch>();

    const [formState, setFormState] = useState<UserSettingsDTO>({
        email,
        areNotificationsEnabled,
        theme,
    });

    const prevValueRef = useRef<UserSettingsDTO | null>(null);
    const isFirstRender = useRef(true);

    const [error, setError] = useState('');
    const [isSaving, setIsSaving] = useState(false);

    const saveSettings = useCallback(
        debounce(async (updatedSettings: UserSettingsDTO) => {
            try {
                setIsSaving(true);
                
                Logger.info("Saving settings");
                
                Email.of(updatedSettings.email); // throws if invalid
                await dispatch(updateUserSettings(updatedSettings));
                
                setIsSaving(false);
            } catch (err: any) {
                Logger.error("Failed to save settings", err);
                setError(err.message || "Failed to save");
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
            setValue: (theme: ThemeOption) =>
                setFormState({ ...formState, theme }),
        },
        isSaving,
    };
};
