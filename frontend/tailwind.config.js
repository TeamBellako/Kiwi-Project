/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{js,ts,jsx,tsx}",
    ],
    theme: {
        extend: {
            colors: {
                kiwiPrimary: '#4CAF50',
                kiwiSecondary: '#8BC34A',
                kiwiTertiary: '#20FFFFFF',
                kiwiResult: '#2E7D32',
                kiwiError: '#F44336',
            },
        },
    },
    plugins: {
        '@tailwindcss/postcss': {},
        autoprefixer: {},
    },
}
