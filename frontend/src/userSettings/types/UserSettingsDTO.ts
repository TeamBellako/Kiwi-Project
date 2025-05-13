import {ThemeOption, UserSettings} from "./UserSettings";
import {Email} from "../../users/Email";

export interface UserSettingsDTO {
    email: string;
    areNotificationsEnabled: boolean;
    theme: ThemeOption;
}

export const toDomainObject = (dto: UserSettingsDTO): UserSettings => ({
    email: Email.of(dto.email),
    areNotificationsEnabled: dto.areNotificationsEnabled,
    theme: dto.theme,
});