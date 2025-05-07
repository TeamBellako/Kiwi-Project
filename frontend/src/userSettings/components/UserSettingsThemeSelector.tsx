import React from "react";

type UserSettingsThemeSelectorProps = {
    value: 'LIGHT' | 'DARK';
    setValue: (theme: 'LIGHT' | 'DARK') => void;
};

const UserSettingsThemeSelector: React.FC<UserSettingsThemeSelectorProps> = ({ value, setValue }) => (
    <fieldset>
        <legend className="block font-medium mb-1">Theme</legend>
        <label className="flex items-center gap-2">
            <input
                type="radio"
                name="theme"
                value="LIGHT"
                checked={value.toUpperCase() === 'LIGHT'}
                onChange={() => setValue('LIGHT')}
            />
            Light
        </label>
        <label className="flex items-center gap-2">
            <input
                type="radio"
                name="theme"
                value="DARK"
                checked={value.toUpperCase() === 'DARK'}
                onChange={() => setValue('DARK')}
            />
            Dark
        </label>
    </fieldset>
);

export default UserSettingsThemeSelector;
