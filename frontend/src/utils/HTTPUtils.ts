export function getServerErrorMessage(error: any, defaultMessage: string) : string {
    return error.response?.status === 500
        ? 'Internal server error. Try again later.'
        : error.response?.data?.error || defaultMessage;
}