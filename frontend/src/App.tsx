import {BrowserRouter, Navigate, Route, Routes} from 'react-router-dom';
import {Provider} from 'react-redux';
import UserSettingsPage from "./userSettings/components/UserSettingsPage";
import UsersPage from "./users/UsersPage";
import {store} from "./store/Store";
import RequireAuth from "./navigation/RequireAuth";
import {ROUTES} from "./navigation/Routes";

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
