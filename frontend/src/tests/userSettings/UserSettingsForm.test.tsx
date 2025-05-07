import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import {axe, toHaveNoViolations} from 'jest-axe';
import {invalidUserSettings, validUserSettings} from "./UserSettingsTestFactory";
import UserSettingsForm from "../../userSettings/components/UserSettingsForm";
import {Provider} from "react-redux";
import {store} from "../../store/Store";
import {getFormFields} from "./UserSettingsTestUtils";

expect.extend(toHaveNoViolations);

let formFields: ReturnType<typeof getFormFields>;

describe('UserSettings Form Tests', () => {
    beforeEach(() => {
        render(
            <Provider store={store}>
                <UserSettingsForm />
            </Provider>
        )
        formFields = getFormFields();
    })
    
    describe('Email Field Tests', () => {
        test('email field gets valid input and accept it', async () => {
            const emailInput = formFields.getEmailInput();
            await userEvent.type(emailInput, validUserSettings.email);
            
            expect(emailInput).toHaveValue(validUserSettings.email);
        });
    
        test('email field gets invalid input and rejects it, showing an error message', async () => {
            const emailInput = formFields.getEmailInput();
            await userEvent.type(emailInput, invalidUserSettings.email);
            emailInput.blur();
            
            const error = await screen.findByText(/invalid email/i);
            expect(error).toBeInTheDocument();
        });
    })
    
    describe('Notifications Toggle Tests', () => {
        test('notifications switches toggles between on and off', async () => {
            const toggle = formFields.getNotificationToggle();
            expect(toggle).not.toBeChecked();
            
            await userEvent.click(toggle);
            expect(toggle).toBeChecked();
            
            await userEvent.click(toggle);
            expect(toggle).not.toBeChecked();
        });
    })
    
    describe('Theme Selector Tests', () => {
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
    })
    
    describe('General Tests', () => {
        it('has no accessibility violations', async () => {
            const { container } = render(
                <Provider store={store}>
                    <UserSettingsForm />
                </Provider>
            );
            
            const results = await axe(container);
            
            expect(results).toHaveNoViolations();
        });
    })
})