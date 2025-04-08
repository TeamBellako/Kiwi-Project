import {useDispatch, useSelector} from "react-redux";
import {AppDispatch, RootState} from "../store";
import {fetchHelloMessage} from "../store/helloSlice";
import {useEffect} from "react";

interface HelloCardProps {
    id: number;
}

const HelloCard: React.FC<HelloCardProps> = ({ id }) => {
    const dispatch = useDispatch<AppDispatch>();
    const { value, status } = useSelector((state: RootState) => state.hello);
    
    useEffect(() => {
        if (status === 'idle') {
            dispatch(fetchHelloMessage(id));
        }
    }, [status, id, dispatch]);
    
    if (status === 'loading') {
        return <div>Loading...</div>
    }
    
    if (status === 'failed') {
        return <div>Error fetching message</div>;
    }
    
    return (
        <div className="bg-blue-500 text-white p-4 rounded">
            <h3>Message from Back-End:</h3>
            <p>{value}</p>
        </div>
    )
}

export default HelloCard;