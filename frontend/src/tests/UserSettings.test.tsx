import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {Provider} from 'react-redux';
import UserSettingsPage from "../userSettings/components/UserSettingsPage";
import {configureStore, Store, UnknownAction} from '@reduxjs/toolkit';
import api from "../services/api";
import {userSettingsReducer} from "../userSettings/store/UserSettingsSlice";
import {userSettingsLabels} from "./TestLabels";
import {BrowserRouter} from "react-router-dom";
import {UserSettingsDTO} from "../userSettings/types/UserSettingsDTO";

jest.mock("../services/api");

const loadingMessage = "Loading settings...";
const errorMessageWithSensibleInformation = "Adventure Time > Steven Universe";

const validUserSettingsDTO: UserSettingsDTO = {
    email: 'finn@thehuman.com',
    areNotificationsEnabled: true,
    theme: 'DARK'
};

const updateUserSettingsDTO: UserSettingsDTO = {
    email: 'finn@thehuman.com',
    areNotificationsEnabled: false,
    theme: 'LIGHT'
};

describe('UserSettings Tests', () => {
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
            <BrowserRouter>
                <Provider store={store}>
                    <UserSettingsPage />
                </Provider>
            </BrowserRouter>
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
    
    test('load data', () => {
        api.get = jest.fn().mockImplementation(() => new Promise(() => {}));
        renderUserSettingsPage();

        expect(screen.getByText(loadingMessage)).toBeInTheDocument();
    });

    test('display data', async () => {
        mockApiGetRequest(validUserSettingsDTO);
        renderUserSettingsPage();

        await waitFor(() => {
            expect(screen.getByLabelText(/Dark/i)).toBeChecked();
        });
    });

    test('server error', async () => {
        mockApiGetErrorRequest(errorMessageWithSensibleInformation);
        renderUserSettingsPage();

        await waitFor(() => {
            expect(screen.getByText(/Server Error:/i)).toBeInTheDocument();
            // We don't want sensible information contained in the server error message to be displayed
            expect(screen.queryByText(errorMessageWithSensibleInformation)).toBeNull();
        });
    });
    
    test('saveSettings', async () => {
        mockApiGetRequest(validUserSettingsDTO);
        renderUserSettingsPage();
        
        mockApiPutRequest(updateUserSettingsDTO);
        await waitFor(() => {
            fireEvent.click(screen.getByLabelText(userSettingsLabels.notifications),
                {target: {value: updateUserSettingsDTO.areNotificationsEnabled}});
        });
        await waitFor(() => {
            expect(screen.getByLabelText(userSettingsLabels.notifications)).not.toBeChecked();
            expect(api.put).toHaveBeenCalledTimes(1);
        });

        mockApiPutRequest(validUserSettingsDTO);
        await waitFor(() => {
            fireEvent.click(screen.getByLabelText(userSettingsLabels.notifications),
                {target: {value: updateUserSettingsDTO.areNotificationsEnabled}});
        });
        await waitFor(() => {
            expect(screen.getByLabelText(userSettingsLabels.notifications)).toBeChecked();
            expect(api.put).toHaveBeenCalledTimes(1);
        });
    });

    test('saveSettings with no changes', async () => {
        mockApiGetRequest(validUserSettingsDTO);
        mockApiPutRequest(validUserSettingsDTO);
        renderUserSettingsPage();
        
        await waitFor(() => {
            fireEvent.change(screen.getByLabelText(userSettingsLabels.notifications), { target: { value: validUserSettingsDTO.email } });
        });
        
        await waitFor(() => {
            expect(api.put).toHaveBeenCalledTimes(0);
        });
    });

    test('saveSettings too frequently', async () => {
        mockApiGetRequest(validUserSettingsDTO);
        renderUserSettingsPage();
        
        await waitFor(() => {
            fireEvent.click(screen.getByLabelText(userSettingsLabels.notifications), {target: {value: true}});
            fireEvent.click(screen.getByLabelText(userSettingsLabels.notifications), {target: {value: false}});
            fireEvent.click(screen.getByLabelText(userSettingsLabels.notifications), {target: {value: true}});
            fireEvent.click(screen.getByLabelText(userSettingsLabels.notifications), {target: {value: false}});
        });
        
        await waitFor(() => {
            expect(api.put).toHaveBeenCalledTimes(1);
        });
    });
});