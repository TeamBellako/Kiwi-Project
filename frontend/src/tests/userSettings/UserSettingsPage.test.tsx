import {render, screen, waitFor, fireEvent, act} from '@testing-library/react';
import { Provider } from 'react-redux';
import UserSettingsPage from "../../userSettings/components/UserSettingsPage";
import { configureStore, Store, UnknownAction } from '@reduxjs/toolkit';
import api from "../../services/api";
import { userSettingsReducer} from "../../userSettings/store/UserSettingsSlice";
import { userSettingsLabels } from "../constants/Labels";

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
        jest.useFakeTimers();

        mockStore = configureStore({
            reducer: {
                userSettings: userSettingsReducer,
            },
        });
    });

    const renderUserSettingsPage = (store = mockStore) => {
        return render(
            <Provider store={store}>
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
        test('should display loading message while loading data', () => {
            api.get = jest.fn().mockImplementation(() => new Promise(() => {}));
            renderUserSettingsPage();

            expect(screen.getByText(loadingMessage)).toBeInTheDocument();
        });

        test('should display user settings when load is successful', async () => {
            mockApiGetRequest(validUserSettings);
            renderUserSettingsPage();

            await waitFor(() => {
                expect(screen.getByLabelText(/Dark/i)).toBeChecked();
            });
        });

        test('should display error message when load fails', async () => {
            mockApiGetErrorRequest(errorMessage);

            renderUserSettingsPage();

            await waitFor(() => {
                expect(screen.getByText(/Server Error:/i)).toBeInTheDocument();
                // We don't want sensible information contained in the server error message to be displayed
                expect(screen.queryByText(errorMessage)).toBeNull();
            });
        });
    });
    
    describe('update tests', () => {
        test('should trigger saveSettings after a debounce delay', async () => {
            mockApiGetRequest(validUserSettings);
            mockApiPutRequest(updateUserSettings);
            renderUserSettingsPage();
            
            await waitFor(() => {
                fireEvent.change(screen.getByLabelText(userSettingsLabels.email), {target: {value: updateUserSettings.email}});
            });
            await act(async () => {
                jest.advanceTimersByTime(600);
            });
            
            await waitFor(() => {
                expect(screen.getByLabelText(userSettingsLabels.email)).toHaveValue(updateUserSettings.email);
                expect(api.put).toHaveBeenCalledTimes(1);
            });
        });

        test('should not trigger saveSettings if there are no changes', async () => {
            mockApiGetRequest(validUserSettings);
            renderUserSettingsPage();
            
            await waitFor(() => {
                fireEvent.change(screen.getByLabelText(userSettingsLabels.notifications), { target: { value: validUserSettings.email } });
            });

            await act(async () => {
                jest.advanceTimersByTime(600);
            });
            
            await waitFor(() => {
                expect(api.put).toHaveBeenCalledTimes(0);
                // Save is getting called even if the state is the same
                throw new Error('Not Implemented');
            });
        });

        test('should not trigger saveSettings too frequently', async () => {
            mockApiGetRequest(validUserSettings);
            renderUserSettingsPage();
            
            await waitFor(() => {
                const emailInput = screen.getByLabelText(userSettingsLabels.email);
                fireEvent.change(emailInput, { target: { value: updateUserSettings.email } });
                fireEvent.change(emailInput, { target: { value: 'marceline@lovessimon.com' } });
                fireEvent.change(emailInput, { target: { value: 'princess@bubblegum.com' } });
            });
            await act(async () => {
                jest.advanceTimersByTime(600);
            });
            
            await waitFor(() => {
                expect(api.put).toHaveBeenCalledTimes(1);
            });
        });

        test('should display error message for invalid email format', async () => {
            mockApiGetRequest(validUserSettings);
            renderUserSettingsPage();
            
            await waitFor(() => {
                const emailInput = screen.getByLabelText(userSettingsLabels.email);
                fireEvent.change(emailInput, { target: { value: invalidUserSettings.email } });
            });

            await waitFor(() => {
                expect(screen.getByText(invalidEmailError)).toBeInTheDocument();
                expect(api.put).toHaveBeenCalledTimes(0);
            });
        });

        test('should handle server failure during settings update', async () => {
            mockApiPutErrorRequest(errorMessage);
            mockApiGetRequest(validUserSettings);
            
            renderUserSettingsPage();
            await waitFor(() => {
                fireEvent.change(screen.getByLabelText(userSettingsLabels.email), {target: {value: updateUserSettings.email}});
            });
            await act(async () => {
                jest.advanceTimersByTime(600);
            });

            await waitFor(() => {
                expect(screen.getByText(serverErrorMessage)).toBeInTheDocument();
                // We don't want sensible information contained in the server error message to be displayed
                expect(screen.queryByText(errorMessage)).toBeNull();
            });
        });

        test('should not trigger saveSettings after loadSettings', async () => {
            throw new Error('Not Implemented');
        });

        test('should not trigger loadSettings after saveSettings', async () => {
            throw new Error('Not Implemented');
        });
    });
});