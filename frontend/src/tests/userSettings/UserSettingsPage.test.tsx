import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {Provider} from 'react-redux';
import UserSettingsPage from "../../userSettings/components/UserSettingsPage";
import {configureStore, Store, UnknownAction} from '@reduxjs/toolkit';
import api from "../../services/api";
import {userSettingsReducer} from "../../userSettings/store/UserSettingsSlice";
import {userSettingsLabels} from "../constants/Labels";
import {invalidUserSettings, updateUserSettings, validUserSettings} from "./UserSettingsTestFactory";

jest.mock("../../services/api");

const loadingMessage = "Loading settings...";
const invalidEmailError = "Invalid email format";
const errorMessageWithSensibleInformation = "Adventure Time > Steven Universe";

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

        test('should display hardcoded error message when load fails with a 500 response', async () => {
            mockApiGetErrorRequest(errorMessageWithSensibleInformation);
            renderUserSettingsPage();

            await waitFor(() => {
                expect(screen.getByText(/Server Error:/i)).toBeInTheDocument();
                // We don't want sensible information contained in the server error message to be displayed
                expect(screen.queryByText(errorMessageWithSensibleInformation)).toBeNull();
            });
        });
    });
    
    describe('update tests', () => {
        test('should trigger saveSettings after a debounce delay', async () => {
            mockApiGetRequest(validUserSettings);
            renderUserSettingsPage();
            
            mockApiPutRequest(updateUserSettings);
            await waitFor(() => {
                fireEvent.change(screen.getByLabelText(userSettingsLabels.email), {target: {value: updateUserSettings.email}});
            });
            await waitFor(() => {
                expect(screen.getByLabelText(userSettingsLabels.email)).toHaveValue(updateUserSettings.email);
                expect(api.put).toHaveBeenCalledTimes(1);
            });

            mockApiPutRequest(validUserSettings);
            await waitFor(() => {
                fireEvent.change(screen.getByLabelText(userSettingsLabels.email), {target: {value: validUserSettings.email}});
            });
            await waitFor(() => {
                expect(screen.getByLabelText(userSettingsLabels.email)).toHaveValue(validUserSettings.email);
                expect(api.put).toHaveBeenCalledTimes(1);
            });
        });

        test('should not trigger neither saveSettings if there are no changes', async () => {
            mockApiGetRequest(validUserSettings);
            mockApiPutRequest(validUserSettings);
            renderUserSettingsPage();
            
            await waitFor(() => {
                fireEvent.change(screen.getByLabelText(userSettingsLabels.notifications), { target: { value: validUserSettings.email } });
            });
            
            await waitFor(() => {
                expect(api.put).toHaveBeenCalledTimes(0);
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
            
            await waitFor(() => {
                expect(api.put).toHaveBeenCalledTimes(1);
            });
        });

        test('should display error message for invalid email format and then hide it when email is valid again', async () => {
            mockApiGetRequest(validUserSettings);
            renderUserSettingsPage();
            
            await waitFor(() => {
                fireEvent.change(screen.getByLabelText(userSettingsLabels.email), { target: { value: invalidUserSettings.email } });
            });
            await waitFor(() => {
                expect(screen.getByText(invalidEmailError)).toBeInTheDocument();
                expect(api.put).toHaveBeenCalledTimes(0);
            });

            await waitFor(() => {
                fireEvent.change(screen.getByLabelText(userSettingsLabels.email), { target: { value: validUserSettings.email } });
            });
            await waitFor(() => {
                expect(screen.queryByText(invalidEmailError)).toBeNull();
            });
        });

        test('should trigger loading if update fails and show a hardcoded error message when receiving a 500 response', async () => {
            mockApiGetRequest(validUserSettings);
            mockApiPutErrorRequest(errorMessageWithSensibleInformation);
            renderUserSettingsPage();
            
            await waitFor(() => {
                fireEvent.change(screen.getByLabelText(userSettingsLabels.email), {target: {value: updateUserSettings.email}});
            });

            await waitFor(() => {
                expect(screen.getByText(/Server Error:/i)).toBeInTheDocument();
                // We don't want sensible information contained in the server error message to be displayed
                expect(screen.queryByText(errorMessageWithSensibleInformation)).toBeNull();
            });
        });
    });
});