import {ReactNode} from 'react';
import {useSelector} from 'react-redux';
import {Navigate, useLocation} from 'react-router-dom';
import {RootState} from "../store/Store";
import {ROUTES} from "./Routes";

interface RequireAuthProps {
    children: ReactNode;
}

const RequireAuth = ({ children }: RequireAuthProps) => {
    const token = useSelector((state: RootState) => state.users.token);
    const location = useLocation();

    if (!token) {
        return <Navigate to={ROUTES.LOGIN} state={{ from: location }} replace />;
    }

    return <>{children}</>;
};

export default RequireAuth;
