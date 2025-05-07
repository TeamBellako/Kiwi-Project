type LogLevel = 'debug' | 'info' | 'warn' | 'error';

const isProd = import.meta.env.VITE_FRONT_ENV === 'prod';

const log = (level: LogLevel, message: string, ...optionalParams: unknown[]) => {
    if (isProd && level === 'debug') return;

    const timestamp = new Date().toISOString();
    console[level](`[${timestamp}] [${level.toUpperCase()}] ${message}`, ...optionalParams);
};

export const Logger = {
    debug: (msg: string, ...params: unknown[]) => log('debug', msg, ...params),
    info: (msg: string, ...params: unknown[]) => log('info', msg, ...params),
    warn: (msg: string, ...params: unknown[]) => log('warn', msg, ...params),
    error: (msg: string, ...params: unknown[]) => log('error', msg, ...params),
};
