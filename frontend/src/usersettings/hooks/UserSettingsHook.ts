import {useState, useCallback, useEffect, useRef} from "react";
import { ThemeOption, UserSettings } from "../types/UserSettings";
import { isValidEmail } from "../../utils/ValidationUtils";
import {debounce, isEqual} from "lodash";
import { useDispatch } from "react-redux";
import { updateUserSettings } from "../store/UserSettingsThunks";
import {AppDispatch} from "../../store/Store";

type UserSettingsFormProps = Partial<UserSettings>;

export const useUserSettingsForm = ({
    id = 1, // TODO: Remove when JWT is implemented
    email = '',
    areNotificationsEnabled = false,
    theme = 'light',
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

    const [fieldError, setFieldError] = useState('');
    const [serverError, setServerError] = useState<string>('');
    const [isSaving, setIsSaving] = useState(false);
    
    const validateEmail = () => {
        if (!isValidEmail(email)) {
            setFieldError("Invalid Email");
            return false;
        }
        return true;
    };
    
    const saveSettings = useCallback(
        debounce(async (updatedSettings: UserSettings) => {
            try {
                setIsSaving(true);
                await dispatch(updateUserSettings(updatedSettings));
                setIsSaving(false);
            } catch (err) {
                setServerError('Internal server error, try again later');
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

        // Prevent invalid input from triggering save
        if (value.email && !validateEmail()) return;

        // Skip if no changes
        if (isEqual(prevValueRef.current, value)) return;

        console.log('💾 Saving settings:', value);

        prevValueRef.current = value;
        saveSettings(value);
    }, [value]);

    return {
        emailField: {
            value: value.email,
            setValue: (email: string) => setValue({ ...value, email }),
            fieldError: fieldError,
            setFieldError: setFieldError,
            validate: validateEmail,
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
        isSaving,
        serverError,
    };
};
