module.exports = {
    theme: {
        extend: {
            fontFamily: {
                roboto: ["Roboto", "sans-serif"], // Using 'sans' as an example
                courgette: ["Courgette", "cursive"],
            },
        },
    },
    plugins: [
        require("tailwindcss"),
        require("postcss-preset-env")({
            stage: 0,
        }),
        function ({ addUtilities }) {
            addUtilities({
                '.scrollbar-hide': {
                    /* Firefox */
                    'scrollbar-width': 'none',
                    /* Internet Explorer 10+ */
                    '-ms-overflow-style': 'none',
                    /* Safari and Chrome */
                    '&::-webkit-scrollbar': {
                        display: 'none'
                    }
                }
            });
        }
    ],
    purge: ["./src/**/*.{js,jsx,ts,tsx}"],
};