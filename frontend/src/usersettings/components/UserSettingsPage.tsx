import {useEffect} from 'react';
import UserSettingsForm from './UserSettingsForm';
import {loadUserSettings} from "../store/UserSettingsThunks";
import {useAppDispatch, useAppSelector} from "../../store/Hooks";
import {selectUserSettings, selectUserSettingsError, selectUserSettingsStatus} from "../store/UserSettingsSelector";
import UserSettingsLoadingPage from "./UserSettingsLoadingPage";
import UserSettingsErrorPage from "./UserSettingsErrorPage";

const UserSettingsPage = () => {
    const dispatch = useAppDispatch();

    const userSettings = useAppSelector(selectUserSettings);
    const status = useAppSelector(selectUserSettingsStatus);
    const error = useAppSelector(selectUserSettingsError);

    useEffect(() => {
        dispatch(loadUserSettings());
    }, []);

    if (status === 'loading') {
        return <UserSettingsLoadingPage />;
    }

    if (error) {
        return <UserSettingsErrorPage error={error} />;
    }

    return (
        <div className="p-4 max-w-lg mx-auto">
            <h1 className="text-2xl font-bold mb-4">User Settings</h1>
            {userSettings && (
                <UserSettingsForm
                    email={userSettings.email}
                    areNotificationsEnabled={userSettings.areNotificationsEnabled}
                    theme={userSettings.theme}
                />
            )}
        </div>
    );
};

export default UserSettingsPage;