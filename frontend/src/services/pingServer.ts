import api from "./api";

export const pingServer = async (): Promise<boolean> => {
    const controller = new AbortController();

    const timeout = setTimeout(() => controller.abort(), 1000);

    try {
        await api.get('/api/ping', { signal: controller.signal });
        return true;
    } catch (error) {
        return false;
    } finally {
        clearTimeout(timeout);
    }
};
