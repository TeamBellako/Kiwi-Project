export const THEMES = ['light', 'dark'] as const;
export type ThemeOption = (typeof THEMES)[number];

export interface UserSettings {
    id: number; // TODO: Remove when JWT is implemented
    email: string;
    areNotificationsEnabled: boolean;
    theme: ThemeOption;
}