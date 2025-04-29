export const THEMES = ['light', 'dark'] as const;
export type ThemeOption = (typeof THEMES)[number];

export interface UserSettings {
    email: string;
    areNotificationsEnabled: boolean;
    theme: ThemeOption;
}