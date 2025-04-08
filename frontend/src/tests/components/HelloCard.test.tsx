import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import HelloCard from "../../components/HelloCard";
import api from "../../services/api";
import helloReducer from "../../store/helloSlice";
import {configureStore, Store, UnknownAction} from "@reduxjs/toolkit";

jest.mock("../../services/api");

describe('HelloCard Component', () => {
    let mockStore: Store<unknown, UnknownAction, unknown>;

    beforeEach(() => {
        jest.resetAllMocks();
        
        mockStore = configureStore({
            reducer: {
                hello: helloReducer
            }
        });
    });
    
    test('displays loading message while fetching data', () => {
        api.get = jest.fn().mockImplementation(() => new Promise(() => {}));

        render(
            <Provider store={mockStore}>
                <HelloCard id={1} />
            </Provider>
        );

        expect(screen.getByText(/Loading.../i)).toBeInTheDocument();
    });
    

    test('displays hello message when fetch is successful', async () => {
        api.get = jest.fn().mockResolvedValue({ data: { message: 'Hello World!', status: 'succeeded' } });

        render(
            <Provider store={mockStore}>
                <HelloCard id={1} />
            </Provider>
        );

        await waitFor(() => expect(screen.getByText(/Hello World!/i)).toBeInTheDocument());
    });

    test('displays error message when fetch fails', async () => {
        api.get = jest.fn().mockRejectedValue({ data: { message: '', status: 'failed' } });

        render(
            <Provider store={mockStore}>
                <HelloCard id={-1} />
            </Provider>
        );

        await waitFor(() => expect(screen.getByText(/Error fetching message/i)).toBeInTheDocument());
    });
});
