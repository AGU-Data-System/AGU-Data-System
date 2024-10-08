import * as React from 'react';
import { useEffect, useState } from 'react';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import { Outlet } from 'react-router-dom';
import ThemeToggleSwitch from './ThemeToggleSwitch'; // Import the custom switch

interface NavBarProps {
    isDarkMode: boolean;
    toggleTheme: () => void;
}

export default function NavBar({ isDarkMode, toggleTheme }: NavBarProps) {
    const [currentDate, setCurrentDate] = useState(new Date());

    useEffect(() => {
        const intervalId = setInterval(() => {
            setCurrentDate(new Date());
        }, 1000);

        return () => clearInterval(intervalId);
    }, []);

    const formattedDate = `${currentDate.getDate().toString().padStart(2, '0')}/${(currentDate.getMonth() + 1).toString().padStart(2, '0')}/${currentDate.getFullYear()}`;
    const formattedTime = `${currentDate.getHours().toString().padStart(2, '0')}:${currentDate.getMinutes().toString().padStart(2, '0')}`;

    return (
        <div style={{ minHeight: '100%' }}>
            <AppBar position="static" sx={{ backgroundColor: 'rgb(255, 165, 0)', color: 'black' }}>
                <Toolbar>
                    <Typography variant="h4">
                        Data: {formattedDate}
                    </Typography>
                    <Typography variant="h4" sx={{ marginLeft: '20px' }}>
                        Hora: {formattedTime}
                    </Typography>
                    <Typography variant="h4" component="div" sx={{ flexGrow: 1, textAlign: 'right', marginRight: 2 }}>
                        Planeamento & Controlo GL UAGs
                    </Typography>
                    <ThemeToggleSwitch isDarkMode={isDarkMode} toggleTheme={toggleTheme} />
                </Toolbar>
            </AppBar>
            <Outlet />
        </div>
    );
}