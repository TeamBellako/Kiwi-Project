export const tryGetJWTToken = (): string | null => {
    return localStorage.getItem('jwtToken');
}

export const clearJWTToken = () => {
    localStorage.removeItem('jwtToken');
}