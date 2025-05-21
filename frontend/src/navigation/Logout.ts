import {useDispatch} from 'react-redux';
import {useNavigate} from 'react-router-dom';
import {AppDispatch} from "../store/Store";
import {logout} from "../users/UsersSlice";

export const useAuth = () => {
    const dispatch = useDispatch<AppDispatch>();
    const navigate = useNavigate();

    const logoutUser = () => {
        dispatch(logout());
        navigate('/login');
    };

    return { logoutUser };
};
