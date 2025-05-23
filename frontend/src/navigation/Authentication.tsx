import {ReactNode} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import {Navigate, useLocation, useNavigate} from 'react-router-dom';
import {AppDispatch, RootState} from "../store/Store";
import {ROUTES} from "./Routes";
import {logout} from "../users/UsersSlice";

interface RequireAuthProps {
    children: ReactNode;
}

export const RequireAuth = ({ children }: RequireAuthProps) => {
    const token = useSelector((state: RootState) => state.users.token);
    const location = useLocation();

    if (!token) {
        return <Navigate to={ROUTES.LOGIN} state={{ from: location }} replace />;
    }

    return <>{children}</>;
};

export const useAuth = () => {
    const dispatch = useDispatch<AppDispatch>();
    const navigate = useNavigate();

    const logoutUser = () => {
        dispatch(logout());
        navigate('/login');
    };

    return { logoutUser };
};