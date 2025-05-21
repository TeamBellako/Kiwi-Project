import {configureStore, Store, UnknownAction} from "@reduxjs/toolkit";
import {usersReducer} from "../../users/UsersSlice";
import {render, screen} from "@testing-library/react";
import {Provider} from "react-redux";
import UsersPage from "../../users/UsersPage";
import api from "../../services/api";
import {UsersDTO} from "../../users/UsersDTO";
import userEvent from "@testing-library/user-event";

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
        userEvent.click(screen.getByRole('button', { name: /signup/i }));

        const successMessage = await screen.findByRole('result');
        expect(successMessage).toBeVisible();
    });

    test('invalid signup', () => {

    });

    test('duplicated signup', () => {

    });

    test('valid login', () => {

    });

    test('invalid login', () => {

    });

    test('incorrect login', () => {

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