import {toDomainObject, UsersDTO} from "./UsersDTO";
import {useDispatch} from "react-redux";
import {useState} from "react";
import {login, signup} from "./UsersThunks";
import {useNavigate} from "react-router-dom";
import {RetryAction} from "./UsersState";
import {selectUsersRetryAction} from "./UsersSelector";
import {AppDispatch} from "../../services/store/Store";
import {useAppSelector} from "../../services/store/Hooks";
import {ROUTES} from "../../services/navigation/Routes";

type UsersFormProps = Partial<UsersDTO>;

export const useUsersForm = ({
    email = '',
    password = '',
}: UsersFormProps) => {
    const dispatch = useDispatch<AppDispatch>();
    const navigate = useNavigate();
    const retryAction = useAppSelector(selectUsersRetryAction)

    const [formState, setFormState] = useState<UsersDTO>({
        email,
        password,
    });

    const [error, setError] = useState<Error | null>(null);
    const [result, setResult] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    const updateFormState = (key: keyof UsersDTO, value: string) => {
        setFormState(prev => ({ ...prev, [key]: value }));
    };

    const signupUser = async () => {
        setLoading(true);
        setError(null);
        setResult(null);
        try {
            toDomainObject(formState);
            
            await dispatch(signup(formState)).unwrap();
            
            setResult("New User Successfully Created!");
        } catch (error) {
            setError(Error(error as string));
        } finally {
            setLoading(false);
        }
    };

    const loginUser = async () => {
        setLoading(true);
        setError(null);
        setResult(null);
        try {
            toDomainObject(formState);
            
            await dispatch(login(formState)).unwrap();
            
            navigate(ROUTES.SETTINGS);
        } catch (error) {
            setError(Error(error as string));
        } finally {
            setLoading(false);
        }
    };

    const handleRetry = async () => {
        if (retryAction === RetryAction.LOGIN) {
            await loginUser();
        } else if (retryAction === RetryAction.SIGNUP) {
            await signupUser();
        }
    };

    return {
        formState,
        updateFormState,
        signupUser,
        loginUser,
        error,
        result,
        loading,
        handleRetry
    };
};
