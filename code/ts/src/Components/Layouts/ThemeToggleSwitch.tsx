import * as React from 'react';
import { styled } from '@mui/material/styles';
import Switch from '@mui/material/Switch';

const ThemeSwitch = styled(Switch)(({ theme }) => ({
    width: 50,
    height: 26,
    padding: 0,
    '& .MuiSwitch-switchBase': {
        padding: 1,
        '&.Mui-checked': {
            transform: 'translateX(24px)',
            color: '#fff',
            '& + .MuiSwitch-track': {
                backgroundColor: theme.palette.mode === 'dark' ? '#aab4be' : '#8796A5',
                opacity: 1,
            },
        },
    },
    '& .MuiSwitch-thumb': {
        width: 24,
        height: 24,
        backgroundColor: theme.palette.mode === 'dark' ? '#272626' : '#ffffff',
    },
    '& .MuiSwitch-track': {
        borderRadius: 13,
        backgroundColor: theme.palette.mode === 'dark' ? '#8796A5' : '#aab4be',
        opacity: 1,
    },
}));

interface ThemeToggleSwitchProps {
    isDarkMode: boolean;
    toggleTheme: () => void;
}

const ThemeToggleSwitch: React.FC<ThemeToggleSwitchProps> = ({ isDarkMode, toggleTheme }) => {
    return (
        <ThemeSwitch checked={isDarkMode} onChange={toggleTheme} />
    );
};

export default ThemeToggleSwitch;