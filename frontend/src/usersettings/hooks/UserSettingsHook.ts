import {useCallback, useEffect, useRef, useState} from "react";
import {ThemeOption, UserSettings} from "../types/UserSettings";
import {isValidEmail} from "../../utils/ValidationUtils";
import {debounce, isEqual} from "lodash";
import {useDispatch} from "react-redux";
import {updateUserSettings} from "../store/UserSettingsThunks";
import {AppDispatch} from "../../store/Store";

type UserSettingsFormProps = Partial<UserSettings>;

export const useUserSettingsForm = ({
    id = 1, // TODO: Remove when JWT is implemented
    email = '',
    areNotificationsEnabled = false,
    theme = 'LIGHT',
}: UserSettingsFormProps) => {
    const dispatch = useDispatch<AppDispatch>();

    const [value, setValue] = useState<UserSettings>({
        id,
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
                if (!isEqual(prevValueRef.current, value)) {
                    setIsSaving(true);
                    await dispatch(updateUserSettings(updatedSettings));
                    setIsSaving(false);
                }
            } catch (err) {
                setIsSaving(false);
            }
        }, 500),
        [dispatch]
    );

    useEffect(() => {
        if (isFirstRender.current) {
            isFirstRender.current = false;
            prevValueRef.current = value;
            return;
        }

        if (value.email && !isValidEmail(value.email)) {
            setError('Invalid email format');
            return;
        } else if (error.length > 0) {
            setError('');
        }

        if (!isEqual(prevValueRef.current, value)) {
            prevValueRef.current = value;
            saveSettings(value);
        }
    }, [value]);

    return {
        emailField: {
            value: value.email,
            setValue: (email: string) => setValue({ ...value, email }),
            error: error,
            setError: (error: string) => setError(error),
        },
        notificationsField: {
            enabled: value.areNotificationsEnabled,
            onToggle: () =>
                setValue((prev) => ({
                    ...prev,
                    areNotificationsEnabled: !prev.areNotificationsEnabled,
                })),
        },
        themeField: {
            value: value.theme,
            setValue: (theme: ThemeOption) => setValue({ ...value, theme }),
        },
        isSaving
    };
};
