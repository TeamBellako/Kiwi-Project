import {BrowserRouter, Navigate, Route, Routes} from 'react-router-dom';
import {Provider} from 'react-redux';
import UsersPage from "./users/UsersPage";
import {store} from "./store/Store";
import {ROUTES} from "./navigation/Routes";
import {RequireAuth} from "./navigation/Authentication";
import UserSettingsPage from "./userSettings/UserSettingsPage";

function App() {
    return (
        <Provider store={store}>
            <BrowserRouter>
                <Routes>
                    <Route path={ROUTES.LOGIN} element={<UsersPage />} />
                    <Route
                        path={ROUTES.SETTINGS}
                        element={
                            <RequireAuth>
                                <UserSettingsPage />
                            </RequireAuth>
                        }
                    />

                    <Route path="*" element={<Navigate to={ROUTES.LOGIN} replace />} />
                </Routes>
            </BrowserRouter>
        </Provider>
    );
}

export default App;
