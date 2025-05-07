export const THEMES = ['LIGHT', 'DARK'] as const;
export type ThemeOption = (typeof THEMES)[number];

export interface UserSettings {
    email: string;
    areNotificationsEnabled: boolean;
    theme: ThemeOption;
}

export interface UserSettingsDTO {
    id: number;
    email: string;
    areNotificationsEnabled: boolean;
    theme: ThemeOption;
}

export type UserSettingsState = {
    userSettings: UserSettings | null;
    status: 'idle' | 'loading' | 'succeeded' | 'failed';
    error: string | null;
};

export const initialState: UserSettingsState = {
    userSettings: null,
    status: 'idle',
    error: null,
};

export const toDomainObject = (dto: UserSettingsDTO): UserSettings => ({
    email: dto.email,
    areNotificationsEnabled: dto.areNotificationsEnabled,
    theme: dto.theme,
});

export const toDTO = (userSettings: UserSettings, id: number): UserSettingsDTO => ({
    id,
    email: userSettings.email,
    areNotificationsEnabled: userSettings.areNotificationsEnabled,
    theme: userSettings.theme,
});
