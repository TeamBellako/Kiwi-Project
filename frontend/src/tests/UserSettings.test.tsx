import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {Provider} from 'react-redux';
import UserSettingsPage from '../userSettings/components/UserSettingsPage';
import {configureStore, Store, UnknownAction} from '@reduxjs/toolkit';
import api from '../services/api';
import {userSettingsReducer} from '../userSettings/store/UserSettingsSlice';
import {BrowserRouter} from 'react-router-dom';
import {UserSettingsDTO} from '../userSettings/types/UserSettingsDTO';

jest.mock('../services/api');

const loadingMessage = 'Loading settings...';
const errorMessageWithSensibleInformation = 'Adventure Time > Steven Universe';

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

    test('server error', async () => {
        mockApiGetErrorRequest(errorMessageWithSensibleInformation);
        renderUserSettingsPage();

        await waitFor(() => {
            expect(screen.getByText(/Server Error:/i)).toBeInTheDocument();
            expect(
                screen.queryByText(errorMessageWithSensibleInformation)
            ).toBeNull();
        });
    });

    test('saveSettings', async () => {
        mockApiGetRequest(validUserSettingsDTO);
        renderUserSettingsPage();

        mockApiPutRequest(updateUserSettingsDTO);

        const soundSlider = await screen.findByLabelText(/Sound Volume/i);
        fireEvent.change(soundSlider, { target: { value: updateUserSettingsDTO.soundVolume } });

        await waitFor(() => {
            expect(api.put).toHaveBeenCalledTimes(1);
        });
    });

    test('saveSettings with no changes', async () => {
        mockApiGetRequest(validUserSettingsDTO);
        mockApiPutRequest(validUserSettingsDTO);
        renderUserSettingsPage();

        const emailField = await screen.findByDisplayValue(validUserSettingsDTO.email);
        fireEvent.change(emailField, { target: { value: validUserSettingsDTO.email } });

        await waitFor(() => {
            expect(api.put).toHaveBeenCalledTimes(0);
        });
    });

    test('saveSettings throttled on rapid slider changes', async () => {
        mockApiGetRequest(validUserSettingsDTO);
        renderUserSettingsPage();

        const soundSlider = await screen.findByLabelText(/Sound Volume/i);

        fireEvent.change(soundSlider, { target: { value: 30 } });
        fireEvent.change(soundSlider, { target: { value: 35 } });
        fireEvent.change(soundSlider, { target: { value: 40 } });

        jest.advanceTimersByTime(500);

        await waitFor(() => {
            expect(api.put).toHaveBeenCalledTimes(1);
        });
    });
});
