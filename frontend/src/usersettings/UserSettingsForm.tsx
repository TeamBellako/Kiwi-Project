import React, { useState } from 'react';

const UserSettingsForm: React.FC = () => {
    const [email, setEmail] = useState('');
    const [emailError, setEmailError] = useState('');
    const [notificationsEnabled, setNotificationsEnabled] = useState(false);
    const [theme, setTheme] = useState<'light' | 'dark'>('light');

    const validateEmail = (value: string) => {
        const isValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
        setEmailError(isValid ? '' : 'Invalid email');
    };

    return (
        <form className="space-y-6" data-testid="settings-form">
            {/* Email Field */}
            <div>
                <label htmlFor="email" className="block font-medium">
                    Email
                </label>
                <input
                    id="email"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    onBlur={() => validateEmail(email)}
                    className="mt-1 w-full border p-2 rounded"
                    aria-label="Email"
                />
                {emailError && (
                    <p className="text-red-500 mt-1" role="alert">
                        {emailError}
                    </p>
                )}
            </div>

            {/* Notifications Toggle */}
            <div>
                <label htmlFor="notifications" className="flex items-center gap-2">
                    <input
                        id="notifications"
                        type="checkbox"
                        checked={notificationsEnabled}
                        onChange={() => setNotificationsEnabled(!notificationsEnabled)}
                        aria-label="Notifications"
                    />
                    Enable Notifications
                </label>
            </div>

            {/* Theme Selection */}
            <fieldset>
                <legend className="block font-medium mb-1">Theme</legend>
                <label className="flex items-center gap-2">
                    <input
                        type="radio"
                        name="theme"
                        value="light"
                        checked={theme === 'light'}
                        onChange={() => setTheme('light')}
                        aria-label="Light"
                    />
                    Light
                </label>
                <label className="flex items-center gap-2">
                    <input
                        type="radio"
                        name="theme"
                        value="dark"
                        checked={theme === 'dark'}
                        onChange={() => setTheme('dark')}
                        aria-label="Dark"
                    />
                    Dark
                </label>
            </fieldset>

            {/* Submit (not wired yet) */}
            <button
                type="submit"
                className="bg-blue-600 text-white px-4 py-2 rounded"
                onClick={(e) => e.preventDefault()}
            >
                Save
            </button>
        </form>
    );
};

export default UserSettingsForm;