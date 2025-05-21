export const tryGetJWTToken = (): string | null => {
    return localStorage.getItem('jwtToken');
}