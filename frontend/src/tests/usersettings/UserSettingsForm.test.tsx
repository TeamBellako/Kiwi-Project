import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import UserSettingsForm from "../../usersettings/UserSettingsForm";

describe('UserSettingsFormTest', () => {
    
    test('email field gets valid input and accept it', async () => {
        render(<UserSettingsForm />);
        
        const emailInput = screen.getByLabelText(/Email/i);
        await userEvent.type(emailInput, 'finn@thehuman.com');
        
        expect(emailInput).toHaveValue('finn@thehuman.com');
    });

    test('email field gets invalid input and rejects it, showing an error message', async () => {
        render(<UserSettingsForm />);
        
        const emailInput = screen.getByLabelText(/Email/i);
        await userEvent.type(emailInput, 'invalidemail');
        emailInput.blur();
        const error = await screen.findByText(/invalid email/i);
        
        expect(error).toBeInTheDocument();
    });

    test('notifications switches toggles between on and off', async () => {
        render(<UserSettingsForm />);
        
        const toggle = screen.getByLabelText(/notifications/i);
        expect(toggle).not.toBeChecked();
        
        await userEvent.click(toggle);
        expect(toggle).toBeChecked();
        
        await userEvent.click(toggle);
        expect(toggle).not.toBeChecked();
    });

    test('theme selector only allows one option simultaneously', async () => {
        render(<UserSettingsForm />);
        
        const lightOption = screen.getByLabelText(/light/i);
        const darkOption = screen.getByLabelText(/dark/i);

        await userEvent.click(darkOption);
        expect(darkOption).toBeChecked();
        expect(lightOption).not.toBeChecked();

        await userEvent.click(lightOption);
        expect(lightOption).toBeChecked();
        expect(darkOption).not.toBeChecked();
    });
})