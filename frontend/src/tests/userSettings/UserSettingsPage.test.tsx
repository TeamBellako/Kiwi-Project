import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { Provider } from 'react-redux';
import UserSettingsPage from "../../userSettings/components/UserSettingsPage";
import { configureStore, Store, UnknownAction } from '@reduxjs/toolkit';
import api from "../../services/api";
import { userSettingsReducer } from "../../userSettings/store/UserSettingsSlice";

jest.mock("../../services/api");

describe('UserSettingsPage Tests', () => {
    let mockStore: Store<unknown, UnknownAction, unknown>;

    beforeEach(() => {
        jest.resetAllMocks();

        mockStore = configureStore({
            reducer: {
                userSettings: userSettingsReducer,
            },
        });
    });

    describe('load tests', () => {
        test('displays loading message while loading data', () => {
            api.get = jest.fn().mockImplementation(() => new Promise(() => {}));

            render(
                <Provider store={mockStore}>
                    <UserSettingsPage />
                </Provider>
            );

            expect(screen.getByText(/Loading settings.../i)).toBeInTheDocument();
        });

        test('displays user settings when load is successful', async () => {
            const mockSettings = {
                email: 'finn@thehuman.com',
                areNotificationsEnabled: true,
                theme: 'dark',
            };
            api.get = jest.fn().mockResolvedValue({ data: mockSettings });

            render(
                <Provider store={mockStore}>
                    <UserSettingsPage />
                </Provider>
            );

            await waitFor(() => expect(screen.getByText(mockSettings.email)).toBeInTheDocument());
            expect(screen.getByLabelText(/Dark/i)).toBeChecked();
        });

        test('displays error message when fetch fails', async () => {
            api.get = jest.fn().mockRejectedValue(new Error('Failed to load'));

            render(
                <Provider store={mockStore}>
                    <UserSettingsPage />
                </Provider>
            );

            await waitFor(() => expect(screen.getByText(/Error: Failed to load/)).toBeInTheDocument());
        });

        test('dispatches loadUserSettings on mount', async () => {
            const mockSettings = {
                email: 'finn@thehuman.com',
                areNotificationsEnabled: true,
                theme: 'dark',
            };

            api.get = jest.fn().mockResolvedValue({ data: mockSettings });

            render(
                <Provider store={mockStore}>
                    <UserSettingsPage />
                </Provider>
            );

            await waitFor(() => expect(screen.getByText(mockSettings.email)).toBeInTheDocument());
        });

        // New test for server error on load
        test('displays general error message when load fails', async () => {
            api.get = jest.fn().mockRejectedValue(new Error('Connection failed'));

            render(
                <Provider store={mockStore}>
                    <UserSettingsPage />
                </Provider>
            );

            await waitFor(() => expect(screen.getByText(/Failed to load settings/i)).toBeInTheDocument());
        });
    });

    describe('update tests', () => {
        test('update shows results when successful', async () => {
            const mockSettings = {
                email: 'finn@thehuman.com',
                areNotificationsEnabled: true,
                theme: 'dark',
            };

            api.put = jest.fn().mockResolvedValue({ data: mockSettings });

            render(
                <Provider store={mockStore}>
                    <UserSettingsPage />
                </Provider>
            );

            const emailInput = screen.getByLabelText(/Email/i);
            fireEvent.change(emailInput, { target: { value: 'updated@thehuman.com' } });

            const saveButton = screen.getByText(/Save/i);
            fireEvent.click(saveButton);

            await waitFor(() => {
                expect(screen.getByText('updated@thehuman.com')).toBeInTheDocument();
            });
        });

        test('update shows invalid error when trying to update to invalid values', async () => {
            const mockSettings = {
                email: 'finn@thehuman.com',
                areNotificationsEnabled: true,
                theme: 'dark',
            };

            api.put = jest.fn().mockResolvedValue({ data: mockSettings });

            render(
                <Provider store={mockStore}>
                    <UserSettingsPage />
                </Provider>
            );

            const emailInput = screen.getByLabelText(/Email/i);
            fireEvent.change(emailInput, { target: { value: 'invalid-email' } });

            const saveButton = screen.getByText(/Save/i);
            fireEvent.click(saveButton);

            await waitFor(() => {
                expect(screen.getByText(/Invalid email address/i)).toBeInTheDocument();
            });
        });

        test('update forces loading when server error occurs', async () => {
            api.put = jest.fn().mockRejectedValue(new Error('Server error during update'));

            render(
                <Provider store={mockStore}>
                    <UserSettingsPage />
                </Provider>
            );

            const emailInput = screen.getByLabelText(/Email/i);
            fireEvent.change(emailInput, { target: { value: 'updated@thehuman.com' } });

            const saveButton = screen.getByText(/Save/i);
            fireEvent.click(saveButton);

            await waitFor(() => {
                expect(screen.getByText(/Loading.../i)).toBeInTheDocument();
            });

            await waitFor(() => {
                expect(screen.getByText(/Error: Server error during update/i)).toBeInTheDocument();
            });
        });
    });
});
