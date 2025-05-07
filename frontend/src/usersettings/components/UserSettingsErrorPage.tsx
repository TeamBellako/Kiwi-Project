import React from "react";

interface UserSettingsErrorPageProps {
    error: string;
}
const UserSettingsErrorPage: React.FC<UserSettingsErrorPageProps> = ({ error }) => {
    return (
        <div>
            <p className="text-red-600">
                Server Error: {error}
            </p>
        </div>
    );
}

export default UserSettingsErrorPage;