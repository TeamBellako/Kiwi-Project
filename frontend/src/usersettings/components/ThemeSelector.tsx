import React from "react";

type Props = {
    value: 'light' | 'dark';
    setValue: (theme: 'light' | 'dark') => void;
};

const ThemeSelector: React.FC<Props> = ({ value, setValue }) => (
    <fieldset>
        <legend className="block font-medium mb-1">Theme</legend>
        <label className="flex items-center gap-2">
            <input
                type="radio"
                name="theme"
                value="light"
                checked={value.toLowerCase() === 'light'}
                onChange={() => setValue('light')}
            />
            Light
        </label>
        <label className="flex items-center gap-2">
            <input
                type="radio"
                name="theme"
                value="dark"
                checked={value.toLowerCase() === 'dark'}
                onChange={() => setValue('dark')}
            />
            Dark
        </label>
    </fieldset>
);

export default ThemeSelector;
