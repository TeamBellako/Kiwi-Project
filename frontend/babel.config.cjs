module.exports = {
    presets: [
        '@babel/preset-env',
        '@babel/preset-typescript',
        ['@babel/preset-react', { runtime: 'automatic' }],
    ],
    plugins: [
        [
            'babel-plugin-transform-vite-meta-env',
            {
                env: {
                    FRONT_API_URL: 'http://localhost:8080',
                },
            },
        ],
    ],
};
