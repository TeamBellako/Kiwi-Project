import React from 'react';
import HelloCard from "./components/HelloCard";

const App: React.FC = () => {
    return (
        <div className="App">
            <h1>Front-End Message Display</h1>
            <HelloCard id={1} />
        </div>
    );
};

export default App;
