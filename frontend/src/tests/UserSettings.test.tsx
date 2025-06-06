import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {Provider} from 'react-redux';
import {configureStore, Store, UnknownAction} from '@reduxjs/toolkit';
import api from '../services/api';
import {BrowserRouter} from 'react-router-dom';
import {UserSettingsDTO} from "../userSettings/UserSettingsDTO";
import {userSettingsReducer} from "../userSettings/UserSettingsSlice";
import UserSettingsPage from "../userSettings/UserSettingsPage";
import {TestIDs} from "../common/TestIDs";

jest.mock('../services/api');

const validUserSettingsDTO: UserSettingsDTO = {
    email: 'finn@thehuman.com',
    musicVolume: 40,
    soundVolume: 50,
};

const updateUserSettingsDTO: UserSettingsDTO = {
    email: 'finn@thehuman.com',
    musicVolume: 80,
    soundVolume: 90,
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

    const renderUserSettingsPage = async (
        store = mockStore,
    ) => {
        render(
            <BrowserRouter>
                <Provider store={store}>
                    <UserSettingsPage/>
                </Provider>
            </BrowserRouter>
        );

        await waitFor(() => expect(screen.queryByTestId(TestIDs.common.loadingPage)).toBeNull());
    };

    const mockApiGetRequest = (response: any) => {
        api.get = jest.fn().mockResolvedValue({ data: response });
    };

    const mockApiPutRequest = (response: any) => {
        api.put = jest.fn().mockResolvedValue({ data: response });
    };

    test('load data', () => {
        api.get = jest.fn().mockImplementation(() => new Promise(() => {}));

        renderUserSettingsPage();

        expect(screen.getByTestId(TestIDs.common.loadingPage)).toBeVisible();
    });

    test('shows read-only email', async () => {
        mockApiGetRequest(validUserSettingsDTO);
        
        await renderUserSettingsPage();

        expect(screen.getByTestId(TestIDs.userSettings.email).value).toBe(validUserSettingsDTO.email);
    });
    
    test('error on load', async () => {
        api.get = jest.fn().mockRejectedValue({
            response: {
                status: 500,
                data: { message: "Server Error" },
            },
        });

        await renderUserSettingsPage();

        expect(screen.getByTestId(TestIDs.common.errorPage)).toBeVisible();
    });

    test('saveSettings', async () => {
        mockApiGetRequest(validUserSettingsDTO);
        mockApiPutRequest(updateUserSettingsDTO);
        await renderUserSettingsPage();
        
        fireEvent.change(
            screen.getByTestId(TestIDs.userSettings.soundVolume),
            { target: { value: updateUserSettingsDTO.soundVolume } }
        );

        await waitFor(() => {
            expect(api.put).toHaveBeenCalledTimes(1);
        });
    });

    test('saveSettings with no changes', async () => {
        mockApiGetRequest(validUserSettingsDTO);
        mockApiPutRequest(validUserSettingsDTO);
        await renderUserSettingsPage();

        fireEvent.change(
            screen.getByTestId(TestIDs.userSettings.soundVolume),
            { target: { value: validUserSettingsDTO.soundVolume } }
        );

        await waitFor(() => {
            expect(api.put).toHaveBeenCalledTimes(0);
        });
    });

    test('saveSettings throttled on rapid slider changes', async () => {
        mockApiGetRequest(validUserSettingsDTO);
        await renderUserSettingsPage();

        const soundSlider = screen.getByTestId(TestIDs.userSettings.soundVolume);

        fireEvent.change(soundSlider, { target: { value: 30 } });
        fireEvent.change(soundSlider, { target: { value: 35 } });
        fireEvent.change(soundSlider, { target: { value: 40 } });

        jest.advanceTimersByTime(500);

        await waitFor(() => {
            expect(api.put).toHaveBeenCalledTimes(1);
        });
    });
});
