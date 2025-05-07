import React from "react";

type UserSettingsNotificationsToggleProps = {
    enabled: boolean;
    onToggle: () => void;
};

const UserSettingsNotificationsToggle: React.FC<UserSettingsNotificationsToggleProps> = ({ enabled, onToggle }) => (
    <div>
        <label htmlFor="notifications" className="flex items-center gap-2">
            <input
                id="notifications"
                type="checkbox"
                checked={enabled}
                onChange={onToggle}
            />
            Enable Notifications
        </label>
    </div>
);

export default UserSettingsNotificationsToggle;
