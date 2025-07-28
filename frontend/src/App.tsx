import {BrowserRouter, Navigate, Route, Routes} from 'react-router-dom';
import WIPPage from "./WIPPage";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route 
                    path={'/'}
                    element={
                        <WIPPage />
                    } 
                />

                <Route path="*" element={<Navigate to={'/'} replace />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;