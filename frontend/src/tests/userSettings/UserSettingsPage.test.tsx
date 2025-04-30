import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { Provider } from 'react-redux';
import UserSettingsPage from "../../userSettings/components/UserSettingsPage";
import { configureStore, Store, UnknownAction } from '@reduxjs/toolkit';
import api from "../../services/api";
import { userSettingsReducer } from "../../userSettings/store/UserSettingsSlice";
import {userSettingsLabels} from "../constants/Labels";

jest.mock("../../services/api");

const loadingMessage = "Loading settings...";
const errorMessage = "Error: Failed to load";
const invalidEmailError = "Invalid email address";
const serverErrorMessage = "Error: Server error during update";

const validUserSettings = {
    email: 'finn@thehuman.com',
    areNotificationsEnabled: true,
    theme: 'DARK',
};
const updateUserSettings = {
    email: 'jake@thedog.com',
    areNotificationsEnabled: false,
    theme: 'LIGHT',
};
const invalidUserSettings = {
    email: 'bmolovesfootball.com',
    areNotificationsEnabled: true,
    theme: 'DARK',
};

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

    const renderUserSettingsPage = () => {
        return render(
            <Provider store={mockStore}>
                <UserSettingsPage />
            </Provider>
        );
    };

    const mockApiGetRequest = (response: any) => {
        api.get = jest.fn().mockResolvedValue({ data: response });
    };

    const mockApiPutRequest = (response: any) => {
        api.put = jest.fn().mockResolvedValue({ data: response });
    };

    const mockApiGetErrorRequest = (error: any) => {
        api.get = jest.fn().mockRejectedValue(new Error(error));
    };

    const mockApiPutErrorRequest = (error: any) => {
        api.put = jest.fn().mockRejectedValue(new Error(error));
    };
    

    describe('load tests', () => {
        test('displays loading message while loading data', () => {
            api.get = jest.fn().mockImplementation(() => new Promise(() => {}));

            renderUserSettingsPage();

            expect(screen.getByText(loadingMessage)).toBeInTheDocument();
        });

        test('displays user settings when load is successful', async () => {
            mockApiGetRequest(validUserSettings);
            
            renderUserSettingsPage();

            await waitFor(() => {
                expect(screen.getByLabelText(userSettingsLabels.email)).toHaveValue(validUserSettings.email)
                expect(screen.getByLabelText(/Dark/i)).toBeChecked()
            });
        });

        test('displays error message when load fails', async () => {
            mockApiGetErrorRequest(errorMessage);

            renderUserSettingsPage();

            await waitFor(() => {
                expect(screen.getByText(/Server Error:/i)).toBeInTheDocument();
                // We don't want any server error to be directly shown in the UI, since it may contain sensible information
                expect(screen.queryByText(errorMessage)).toBeNull();
            });
        });
    });

    describe('update tests', () => {
        test('update shows results when successful (simulating autosave)', async () => {
            mockApiGetRequest(validUserSettings);
            mockApiPutRequest(updateUserSettings);
            renderUserSettingsPage();

            await waitFor(() => {
                const emailInput = screen.getByLabelText(userSettingsLabels.email);
                fireEvent.change(emailInput, {target: {value: updateUserSettings.email}});
            });

            await waitFor(() => expect(screen.getByLabelText(userSettingsLabels.email)).toHaveValue(updateUserSettings.email))
        });

        test('update shows invalid error when trying to update to invalid values (simulating autosave)', async () => {
            mockApiGetRequest(validUserSettings);
            mockApiPutErrorRequest(invalidEmailError);
            renderUserSettingsPage();

            await waitFor(() => {
                const emailInput = screen.getByLabelText(userSettingsLabels.email);
                fireEvent.change(emailInput, {target: {value: invalidUserSettings.email}});
            });

            await waitFor(() => expect(screen.getByText(invalidEmailError)).toBeInTheDocument())
        });

        test('update forces loading when server error occurs (simulating autosave)', async () => {
            mockApiPutRequest(validUserSettings);

            renderUserSettingsPage();

            const emailInput = screen.getByLabelText(userSettingsLabels.email);
            fireEvent.change(emailInput, { target: { value: 'updated@thehuman.com' } });

            await waitFor(() => {
                expect(screen.getByText(loadingMessage)).toBeInTheDocument();
            });

            mockApiGetErrorRequest(serverErrorMessage);
            await waitFor(() => {
                expect(screen.getByText(serverErrorMessage)).toBeInTheDocument();
            });
        });
    });
});