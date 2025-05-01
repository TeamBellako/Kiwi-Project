import React from "react";

type Props = {
    value: 'LIGHT' | 'DARK';
    setValue: (theme: 'LIGHT' | 'DARK') => void;
};

const ThemeSelector: React.FC<Props> = ({ value, setValue }) => (
    <fieldset>
        <legend className="block font-medium mb-1">Theme</legend>
        <label className="flex items-center gap-2">
            <input
                type="radio"
                name="theme"
                value="light"
                checked={value.toUpperCase() === 'LIGHT'}
                onChange={() => setValue('LIGHT')}
            />
            Light
        </label>
        <label className="flex items-center gap-2">
            <input
                type="radio"
                name="theme"
                value="dark"
                checked={value.toUpperCase() === 'DARK'}
                onChange={() => setValue('DARK')}
            />
            Dark
        </label>
    </fieldset>
);

export default ThemeSelector;
