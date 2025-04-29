import React from "react";

type Props = {
    enabled: boolean;
    onToggle: () => void;
};

const NotificationsToggle: React.FC<Props> = ({ enabled, onToggle }) => (
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

export default NotificationsToggle;
