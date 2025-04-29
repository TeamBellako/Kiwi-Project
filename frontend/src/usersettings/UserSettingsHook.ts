import {useState} from "react";
import {ThemeOption, UserSettings} from "./UserSettings";
import {isValidEmail} from "../utils/ValidationUtils";

type UserSettingsFormProps = Partial<UserSettings>;

export const useUserSettingsForm = ({
    email = '',
    areNotificationsEnabled = false,
    theme = 'light',
}: UserSettingsFormProps) => {
    const [value, setValue] = useState<UserSettings>({
        email,
        areNotificationsEnabled,
        theme,
    });

    const [emailError, setEmailError] = useState('');

    const validateEmail = () => {
        setEmailError(isValidEmail(value.email) ? '' : 'Invalid email');
    };

    return {
        emailField: {
            value: value.email,
            setValue: (email: string) => setValue({ ...value, email }),
            error: emailError,
            setError: setEmailError,
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
    };
};
