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

const mockSettings = {
    email: 'finn@thehuman.com',
    areNotificationsEnabled: true,
    theme: 'dark',
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

    const mockApiErrorRequest = (error: any) => {
        api.get = jest.fn().mockRejectedValue(new Error(error));
    };

    describe('load tests', () => {
        test('displays loading message while loading data', () => {
            api.get = jest.fn().mockImplementation(() => new Promise(() => {}));

            renderUserSettingsPage();

            expect(screen.getByText(loadingMessage)).toBeInTheDocument();
        });

        test('displays user settings when load is successful', async () => {
            mockApiGetRequest(mockSettings);

            renderUserSettingsPage();

            await waitFor(() => expect(screen.getByText(mockSettings.email)).toBeInTheDocument());
            expect(screen.getByLabelText(userSettingsLabels.email)).toHaveValue(mockSettings.email);
            expect(screen.getByLabelText(/Dark/i)).toBeChecked();
        });

        test('displays error message when fetch fails', async () => {
            mockApiErrorRequest(errorMessage);

            renderUserSettingsPage();

            await waitFor(() => expect(screen.getByText(errorMessage)).toBeInTheDocument());
        });

        test('dispatches loadUserSettings on mount', async () => {
            mockApiGetRequest(mockSettings);

            renderUserSettingsPage();

            await waitFor(() => expect(screen.getByText(mockSettings.email)).toBeInTheDocument());
        });

        test('displays general error message when load fails', async () => {
            mockApiErrorRequest('Connection failed');

            renderUserSettingsPage();

            await waitFor(() => expect(screen.getByText(/Failed to load settings/i)).toBeInTheDocument());
        });
    });

    describe('update tests', () => {
        test('update shows results when successful (simulating autosave)', async () => {
            mockApiPutRequest(mockSettings);

            renderUserSettingsPage();

            const emailInput = screen.getByLabelText(userSettingsLabels.email);
            fireEvent.change(emailInput, { target: { value: 'updated@thehuman.com' } });

            await waitFor(() => {
                expect(screen.getByText('updated@thehuman.com')).toBeInTheDocument();
            });
        });

        test('update shows invalid error when trying to update to invalid values (simulating autosave)', async () => {
            mockApiPutRequest(mockSettings);

            renderUserSettingsPage();

            const emailInput = screen.getByLabelText(userSettingsLabels.email);
            fireEvent.change(emailInput, { target: { value: 'invalid-email' } });

            await waitFor(() => {
                expect(screen.getByText(invalidEmailError)).toBeInTheDocument();
            });
        });

        test('update forces loading when server error occurs (simulating autosave)', async () => {
            mockApiPutRequest(mockSettings);

            renderUserSettingsPage();

            const emailInput = screen.getByLabelText(userSettingsLabels.email);
            fireEvent.change(emailInput, { target: { value: 'updated@thehuman.com' } });

            await waitFor(() => {
                expect(screen.getByText(loadingMessage)).toBeInTheDocument();
            });

            mockApiErrorRequest(serverErrorMessage);
            await waitFor(() => {
                expect(screen.getByText(serverErrorMessage)).toBeInTheDocument();
            });
        });
    });
});