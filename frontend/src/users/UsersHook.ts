import {toDomainObject, UsersDTO} from "./UsersDTO";
import {AppDispatch} from "../store/Store";
import {useDispatch} from "react-redux";
import {useState} from "react";
import {signup} from "./UsersThunks";

type UsersFormProps = Partial<UsersDTO>;

export const useUsersForm = ({
    email = '',
    password = '',
}: UsersFormProps) => {
    const dispatch = useDispatch<AppDispatch>();

    const [formState, setFormState] = useState<UsersDTO>({
        email,
        password,
    });

    const [error, setError] = useState<Error | null>(null);
    const [result, setResult] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    const updateFormState = (key: keyof UsersDTO, value: string) => {
        setFormState(prev => ({
            ...prev,
            [key]: value,
        }));
    };

    const signupUser = async (): Promise<void> => {
        setLoading(true);
        setError(null);
        setResult(null);
        try {
            toDomainObject(formState);
            await dispatch(signup(formState));
            setResult("New User Successfully Created!");
        } catch (error) {
            setError(error as Error);
        } finally {
            setLoading(false);
        }
    };

    return {
        formState,
        updateFormState,
        signupUser,
        error,
        result,
        loading,
    };
};
