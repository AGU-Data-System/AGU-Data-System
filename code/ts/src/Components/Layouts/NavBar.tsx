import * as React from 'react';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import {useEffect, useState} from "react";
import {Outlet} from "react-router-dom";

export default function NavBar() {
    const [currentDate, setCurrentDate] = useState(new Date());

    useEffect(() => {
        const intervalId = setInterval(() => {
            setCurrentDate(new Date());
        }, 1000);

        return () => clearInterval(intervalId);
    }, []);

    const formattedDate = `${currentDate.getDate()}/${currentDate.getMonth() + 1}/${currentDate.getFullYear()}`;
    const formattedTime = `${currentDate.getHours()}:${currentDate.getMinutes().toString().padStart(2, '0')}`;

    return (
        <div>
            <AppBar position="static" sx={{ backgroundColor: 'rgb(255, 165, 0)' }}>
                <Toolbar>
                    <Typography variant="h4">
                        Data: {formattedDate}
                    </Typography>
                    <Typography variant="h4" sx={{ marginLeft: '20px' }}>
                        Hora: {formattedTime}
                    </Typography>
                    <Typography variant="h4" component="div" sx={{ flexGrow: 1, textAlign: 'right' }}>
                        Planeamento & Controlo GL UAGs
                    </Typography>
                </Toolbar>
            </AppBar>
            <Outlet />
        </div>
    );
}