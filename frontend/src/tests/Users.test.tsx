import {configureStore, Store, UnknownAction} from "@reduxjs/toolkit";
import {render, screen} from "@testing-library/react";
import {Provider} from "react-redux";
import userEvent from "@testing-library/user-event";
import {UsersDTO} from "../features/users/UsersDTO";
import {usersReducer} from "../features/users/UsersSlice";
import API from "../services/network/API";
import {tryGetJWTToken} from "../services/common/StorageUtils";
import {ROUTES} from "../services/navigation/Routes";
import {TestIDs} from "../services/common/TestIDs";
import UsersPage from "../features/users/UsersPage";

const mockedNavigate = jest.fn();

jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: () => mockedNavigate,
}));

jest.mock("../services/network/API")

describe('Users Tests', () => {
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
        API.post = jest.fn().mockResolvedValue({})

        renderUsersPage()
        await fillFormData(validUserDTO)
        await userEvent.click(screen.getByTestId(TestIDs.users.signup));
        
        expect(screen.getByTestId(TestIDs.users.resultAlert)).toBeVisible();
        expect(API.post).toHaveBeenCalledTimes(1);
        expect(API.post).toHaveBeenCalledWith(expect.stringContaining('signup'), validUserDTO);
    });

    test('invalid signup', async () => {
        API.post = jest.fn();

        renderUsersPage()
        await fillFormData(invalidUserDTO)
        await userEvent.click(screen.getByTestId(TestIDs.users.signup));

        expect(screen.getByTestId(TestIDs.users.errorAlert)).toBeVisible();
        expect(API.post).not.toHaveBeenCalled();
    });

    test('duplicated signup', async () => {
        API.post = jest.fn().mockRejectedValue({
            response: {
                status: 409,
                data: { message: "Conflict" },
            },
        });

        renderUsersPage()
        await fillFormData(validUserDTO)
        await userEvent.click(screen.getByTestId(TestIDs.users.signup));

        expect(screen.getByTestId(TestIDs.users.errorAlert)).toBeVisible();
        expect(API.post).toHaveBeenCalledTimes(1);
        expect(API.post).toHaveBeenCalledWith(expect.stringContaining('signup'), validUserDTO);
    });
    
    test('error on signup', async () => {
        API.post = jest.fn().mockRejectedValue({
            response: {
                status: 500,
                data: { message: "Internal Server Error" },
            },
        });

        renderUsersPage();
        await fillFormData(validUserDTO);
        await userEvent.click(screen.getByTestId(TestIDs.users.signup));
        
        expect(screen.getByTestId(TestIDs.common.errorModal)).toBeVisible();
    });

    test('valid login', async () => {
        const fakeJwt : string = "fake.jwt.token";
        API.post = jest.fn().mockResolvedValue({ data: { jwt: fakeJwt } });

        renderUsersPage();
        await fillFormData(validUserDTO);
        await userEvent.click(screen.getByTestId(TestIDs.users.login));

        expect(tryGetJWTToken()).toBe(fakeJwt);
        expect(mockedNavigate).toHaveBeenCalledWith(ROUTES.SETTINGS);
        expect(API.post).toHaveBeenCalledTimes(1);
        expect(API.post).toHaveBeenCalledWith(expect.stringContaining('login'), validUserDTO);
    });


    test('invalid login', async () => {
        API.post = jest.fn();

        renderUsersPage()
        await fillFormData(invalidUserDTO)
        await userEvent.click(screen.getByTestId(TestIDs.users.login));

        expect(screen.getByTestId(TestIDs.users.errorAlert)).toBeVisible();
        expect(API.post).not.toHaveBeenCalled();
    });

    test('incorrect login', async () => {
        API.post = jest.fn().mockRejectedValue({
            response: {
                status: 401,
                data: { message: "Unauthorized" },
            },
        });

        renderUsersPage()
        await fillFormData(incorrectUserDTO)
        await userEvent.click(screen.getByTestId(TestIDs.users.login));

        expect(screen.getByTestId(TestIDs.users.errorAlert)).toBeVisible();
        expect(API.post).toHaveBeenCalledTimes(1);
        expect(API.post).toHaveBeenCalledWith(expect.stringContaining('login'), incorrectUserDTO);
    });

    const fillFormData = async (dto: UsersDTO) => {
        await userEvent.type(screen.getByTestId(TestIDs.users.email), dto.email);
        await userEvent.type(screen.getByTestId(TestIDs.users.password), dto.password);
    }

    const renderUsersPage = (store = mockStore) => {
        return render(
            <Provider store={store}>
                <UsersPage />
            </Provider>
        );
    };
})
