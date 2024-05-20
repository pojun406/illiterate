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
    ],
    purge: ["./src/**/*.{js,jsx,ts,tsx}"],
};