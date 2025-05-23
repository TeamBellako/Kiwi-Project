import {useEffect} from 'react';
import UserSettingsForm from './UserSettingsForm';
import {loadUserSettings} from "./UserSettingsThunks";
import {useAppDispatch, useAppSelector} from "../store/Hooks";
import {selectUserSettingsDTO, selectUserSettingsError, selectUserSettingsStatus} from "./UserSettingsSelector";
import {Kiwi_InfoBox} from "../ui/components/Kiwi_InfoBox";

const UserSettingsPage = () => {
    const dispatch = useAppDispatch();

    const userSettingsDTO = useAppSelector(selectUserSettingsDTO);
    const status = useAppSelector(selectUserSettingsStatus);
    const error = useAppSelector(selectUserSettingsError);

    useEffect(() => {
        dispatch(loadUserSettings());
    }, []);

    if (status === 'loading') {
        return (
            <Kiwi_InfoBox
                text={"Loading settings..."}
                role={"result"}
                boxColor={"result"}
            />
        );
    }

    if (error) {
        return (
            <Kiwi_InfoBox 
                text={"Server Error:" + error}
                role={"error"}
                boxColor={"error"}
            />
        );
    }

    return (
        <div className="p-4 max-w-lg mx-auto">
            {userSettingsDTO && (
                <UserSettingsForm
                    email={userSettingsDTO.email}
                    soundVolume={userSettingsDTO.soundVolume}
                    musicVolume={userSettingsDTO.musicVolume}
                />
            )}
        </div>
    );
};

export default UserSettingsPage;