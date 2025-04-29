import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import UserSettingsForm from "../../usersettings/UserSettingsForm";
import { userSettingsLabels } from '../constants/Labels';

import { axe, toHaveNoViolations } from 'jest-axe';
import {inValidUserSettings, validUserSettings} from "./UserSettingsTestFactory";
expect.extend(toHaveNoViolations);

describe('UserSettingsFormTest', () => {
    let formFields: ReturnType<typeof getFormFields>;
    const getFormFields = () => {
        const getEmailInput = () =>
            screen.getByRole('textbox', { name: userSettingsLabels.email });
        const getNotificationToggle = () =>
            screen.getByRole('checkbox', { name: userSettingsLabels.notifications });
        const getThemeRadio = (theme: 'light' | 'dark') =>
            screen.getByRole('radio', { name: new RegExp(theme, 'i') });
        
        return {
            getEmailInput,
            getNotificationToggle,
            getThemeRadio
        };
    };
    
    beforeEach(() => {
        render(<UserSettingsForm />)
        formFields = getFormFields();
    })
    
    test('email field gets valid input and accept it', async () => {
        const emailInput = formFields.getEmailInput();
        await userEvent.type(emailInput, validUserSettings.email);
        
        expect(emailInput).toHaveValue(validUserSettings.email);
    });

    test('email field gets invalid input and rejects it, showing an error message', async () => {
        const emailInput = formFields.getEmailInput();
        await userEvent.type(emailInput, inValidUserSettings.email);
        emailInput.blur();
        
        const error = await screen.findByText(/invalid email/i);
        expect(error).toBeInTheDocument();
    });

    test('notifications switches toggles between on and off', async () => {
        const toggle = formFields.getNotificationToggle();
        expect(toggle).not.toBeChecked();
        
        await userEvent.click(toggle);
        expect(toggle).toBeChecked();
        
        await userEvent.click(toggle);
        expect(toggle).not.toBeChecked();
    });

    test('theme selector only allows one option simultaneously', async () => {
        const lightOption = formFields.getThemeRadio("light")
        const darkOption = formFields.getThemeRadio("dark")
        
        await userEvent.click(darkOption);
        expect(darkOption).toBeChecked();
        expect(lightOption).not.toBeChecked();

        await userEvent.click(lightOption);
        expect(lightOption).toBeChecked();
        expect(darkOption).not.toBeChecked();
    });

    it('has no accessibility violations', async () => {
        const { container } = render(<UserSettingsForm />);
        
        const results = await axe(container);
        
        expect(results).toHaveNoViolations();
    });
})