import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {Provider} from 'react-redux';
import {configureStore, Store, UnknownAction} from '@reduxjs/toolkit';
import {BrowserRouter} from 'react-router-dom';
import {SettingsDTO} from '../features/settings/SettingsDTO';
import {settingsReducer} from '../features/settings/SettingsSlice';
import SettingsPage from '../features/settings/SettingsPage';
import {TestIDs} from '../services/common/TestIDs';
import API, {pingServer} from "../services/network/API";

jest.mock('../services/network/API');

const validSettingsDTO: SettingsDTO = {
    email: 'finn@thehuman.com',
    musicVolume: 40,
    soundVolume: 50,
};

const updateSettingsDTO: SettingsDTO = {
    email: 'finn@thehuman.com',
    musicVolume: 80,
    soundVolume: 90,
};

describe('Settings Tests', () => {
    let mockStore: Store<unknown, UnknownAction, unknown>;

    beforeEach(() => {
        jest.resetAllMocks();
        jest.useFakeTimers();

        mockStore = configureStore({
            reducer: {
                settings: settingsReducer,
            },
        });
    });

    const renderSettingsPage = async (
        store = mockStore,
    ) => {
        render(
            <BrowserRouter>
                <Provider store={store}>
                    <SettingsPage/>
                </Provider>
            </BrowserRouter>
        );

        await waitFor(() => expect(screen.queryByTestId(TestIDs.common.loadingModal)).toBeNull());
    };

    const mockApiGetRequest = (response: any) => {
        API.get = jest.fn().mockResolvedValue({ data: response });
    };

    const mockApiPutRequest = (response: any) => {
        API.put = jest.fn().mockResolvedValue({ data: response });
    };

    test('load data', () => {
        API.get = jest.fn().mockImplementation(() => new Promise(() => {}));

        renderSettingsPage();

        expect(screen.getByTestId(TestIDs.common.loadingModal)).toBeVisible();
    });

    test('shows read-only email', async () => {
        mockApiGetRequest(validSettingsDTO);
        
        await renderSettingsPage();

        expect(screen.getByTestId(TestIDs.settings.email).value).toBe(validSettingsDTO.email);
    });
    
    test('error on load', async () => {
        API.get = jest.fn().mockRejectedValue({
            response: {
                status: 500,
                data: { message: "Server Error" },
            },
        });

        await renderSettingsPage();

        expect(screen.getByTestId(TestIDs.common.errorModal)).toBeVisible();
    });

    test('saveSettings', async () => {
        pingServer.mockResolvedValue(true);
        mockApiGetRequest(validSettingsDTO);
        mockApiPutRequest(updateSettingsDTO);
        await renderSettingsPage();

        fireEvent.change(
            screen.getByTestId(TestIDs.settings.soundVolume),
            { target: { value: updateSettingsDTO.soundVolume } }
        );

        await waitFor(() => {
            expect(API.put).toHaveBeenCalledTimes(1);
        });
    });

    test('saveSettings with no changes', async () => {
        pingServer.mockResolvedValue(true);
        mockApiGetRequest(validSettingsDTO);
        mockApiPutRequest(validSettingsDTO);
        await renderSettingsPage();

        fireEvent.change(
            screen.getByTestId(TestIDs.settings.soundVolume),
            { target: { value: validSettingsDTO.soundVolume } }
        );

        await waitFor(() => {
            expect(API.put).toHaveBeenCalledTimes(0);
        });
    });

    test('saveSettings throttled on rapid slider changes', async () => {
        pingServer.mockResolvedValue(true);
        mockApiGetRequest(validSettingsDTO);
        await renderSettingsPage();

        const soundSlider = screen.getByTestId(TestIDs.settings.soundVolume);

        fireEvent.change(soundSlider, { target: { value: 30 } });
        fireEvent.change(soundSlider, { target: { value: 35 } });
        fireEvent.change(soundSlider, { target: { value: 40 } });

        jest.advanceTimersByTime(500);

        await waitFor(() => {
            expect(API.put).toHaveBeenCalledTimes(1);
        });
    });
});
