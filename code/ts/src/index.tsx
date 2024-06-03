import * as React from 'react';
import { createRoot } from 'react-dom/client';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import {Router} from "./router";

const Main = () => {
    const [isDarkMode, setIsDarkMode] = React.useState(false);

    const toggleTheme = () => {
        setIsDarkMode(!isDarkMode);
    };

    const lightTheme = createTheme({
        palette: {
            mode: 'light',
        },
    });

    const darkTheme = createTheme({
        palette: {
            mode: 'dark',
        },
    });

    return (
        <ThemeProvider theme={isDarkMode ? darkTheme : lightTheme}>
            <CssBaseline />
            <Router isDarkMode={isDarkMode} toggleTheme={toggleTheme} />
        </ThemeProvider>
    );
};

const root = createRoot(document.getElementById('root')!);
root.render(<Main />);