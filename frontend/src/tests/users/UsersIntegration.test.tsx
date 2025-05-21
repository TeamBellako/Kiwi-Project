import {configureStore, Store, UnknownAction} from "@reduxjs/toolkit";
import {usersReducer} from "../../users/UsersSlice";
import {render, screen} from "@testing-library/react";
import {Provider} from "react-redux";
import UsersPage from "../../users/UsersPage";
import api from "../../services/api";
import {UsersDTO} from "../../users/UsersDTO";
import userEvent from "@testing-library/user-event";
import {tryGetJWTToken} from "../../utils/StorageUtils";
import {ROUTES} from "../../navigation/Routes"

const mockedNavigate = jest.fn();

jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: () => mockedNavigate,
}));

jest.mock("../../services/api")

describe('Users Integration Tests', () => {
    const validUserDTO : UsersDTO = {
        email: "finn@thehuman.com",
        password: "Math3matical!"
    }
    const invalidUserDTO : UsersDTO = {
        email: "bmolovesfootball.com",
        password: "kk"
    }
    const incorrectUserDTO : UsersDTO = {
        email: "finn@thehuman.com",
        password: "Math3maticalllydawdawdwa!"
    }

    let mockStore: Store<unknown, UnknownAction, unknown>;

    beforeEach(() => {
        jest.resetAllMocks();

        mockStore = configureStore({
            reducer: {
                users: usersReducer,
            },
        });
    });

    test('valid signup', async () => {
        api.post = jest.fn().mockResolvedValue({})

        renderUsersPage()
        await fillFormData(validUserDTO)
        await userEvent.click(screen.getByRole('button', { name: /signup/i }));

        const resultMessage = await screen.findByRole('result');
        expect(resultMessage).toBeVisible();
        expect(api.post).toHaveBeenCalledTimes(1);
        expect(api.post).toHaveBeenCalledWith(expect.stringContaining('signup'), validUserDTO);
    });

    test('invalid signup', async () => {
        api.post = jest.fn();

        renderUsersPage()
        await fillFormData(invalidUserDTO)
        await userEvent.click(screen.getByRole('button', { name: /signup/i }));

        const errorMessage = await screen.findByRole('error');
        expect(errorMessage).toBeVisible();
        expect(api.post).not.toHaveBeenCalled();
    });

    test('duplicated signup', async () => {
        api.post = jest.fn().mockRejectedValue({
            response: {
                status: 409,
                data: { message: "Conflict" },
            },
        });

        renderUsersPage()
        await fillFormData(validUserDTO)
        await userEvent.click(screen.getByRole('button', { name: /signup/i }));

        const errorMessage = await screen.findByRole('error');
        expect(errorMessage).toBeVisible();
        expect(api.post).toHaveBeenCalledTimes(1);
        expect(api.post).toHaveBeenCalledWith(expect.stringContaining('signup'), validUserDTO);
    });

    test('valid login', async () => {
        const fakeJwt : string = "fake.jwt.token";
        api.post = jest.fn().mockResolvedValue({ data: { jwt: fakeJwt } });

        renderUsersPage();
        await fillFormData(validUserDTO);
        await userEvent.click(screen.getByRole('button', { name: /login/i }));

        expect(tryGetJWTToken()).toBe(fakeJwt);
        expect(mockedNavigate).toHaveBeenCalledWith(ROUTES.SETTINGS);
        expect(api.post).toHaveBeenCalledTimes(1);
        expect(api.post).toHaveBeenCalledWith(expect.stringContaining('login'), validUserDTO);
    });


    test('invalid login', async () => {
        api.post = jest.fn();

        renderUsersPage()
        await fillFormData(invalidUserDTO)
        await userEvent.click(screen.getByRole('button', { name: /login/i }));

        const errorMessage = await screen.findByRole('error');
        expect(errorMessage).toBeVisible();
        expect(api.post).not.toHaveBeenCalled();
    });

    test('incorrect login', async () => {
        api.post = jest.fn().mockRejectedValue({
            response: {
                status: 401,
                data: { message: "Unauthorized" },
            },
        });

        renderUsersPage()
        await fillFormData(incorrectUserDTO)
        await userEvent.click(screen.getByRole('button', { name: /login/i }));

        const errorMessage = await screen.findByRole('error');
        expect(errorMessage).toBeVisible();
        expect(api.post).toHaveBeenCalledTimes(1);
        expect(api.post).toHaveBeenCalledWith(expect.stringContaining('login'), incorrectUserDTO);
    });

    const fillFormData = async (dto: UsersDTO) => {
        await userEvent.type(screen.getByLabelText(/email/i), dto.email);
        await userEvent.type(screen.getByLabelText(/password/i), dto.password);
    }

    const renderUsersPage = (store = mockStore) => {
        return render(
            <Provider store={store}>
                <UsersPage />
            </Provider>
        );
    };
})
