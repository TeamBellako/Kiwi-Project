import {BrowserRouter, Navigate, Route, Routes} from 'react-router-dom';
import {Provider} from 'react-redux';
import {store} from "./services/store/Store";
import {ROUTES} from "./services/navigation/Routes";
import UsersPage from "./features/users/UsersPage";
import {RequireAuth} from "./services/navigation/Authentication";
import SettingsPage from "./features/settings/SettingsPage";


function App() {
    return (
        <Provider store={store}>
            <BrowserRouter>
                <Routes>
                    <Route 
                        path={ROUTES.LOGIN}
                        element={
                            <UsersPage />
                        } 
                    />
                    <Route
                        path={ROUTES.SETTINGS}
                        element={
                            <RequireAuth>
                                <SettingsPage />
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