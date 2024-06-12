import * as React from 'react';
import { Button, Badge, Box, Typography, Paper, List, ListItem, ListItemText } from '@mui/material';
import NotificationsNoneOutlinedIcon from '@mui/icons-material/NotificationsNoneOutlined';
import { useEffect, useState } from 'react';
import { AlertsOutputModel } from "../../services/agu/models/alertsOutputModel";
import { useNavigate } from "react-router-dom";

export default function FloatingAlerts({ darkMode }: { darkMode: boolean }) {
    const [alerts, setAlerts] = useState<AlertsOutputModel[]>([]);
    const [showAlerts, setShowAlerts] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchAlerts = async () => {
            //TODO: fetch alerts from API
            const alertsResponse: AlertsOutputModel[] = [
                {
                    aguCui: 1,
                    type: 'Temperature',
                    message: 'Temperature is too high',
                    date: '2024-06-10T14:00:00',
                },
                {
                    aguCui: 2,
                    type: 'Humidity',
                    message: 'Humidity is too low',
                    date: '2024-06-12T14:00:00',
                },
            ];

            setAlerts(alertsResponse);
        }

        fetchAlerts();
    }, []);

    const toggleShowAlerts = () => {
        setShowAlerts(prev => !prev);
    };

    return (
        <div>
            <Button
                onClick={toggleShowAlerts}
                sx={{
                    position: 'fixed',
                    width: 60,
                    height: 60,
                    bottom: 40,
                    right: 40,
                    backgroundColor: 'rgb(255, 165, 0)',
                    color: 'black',
                    borderRadius: 50,
                    textAlign: 'center',
                    '&:hover': {
                        backgroundColor: 'rgba(255,165,0,0.49)',
                    },
                    boxShadow: '2px 2px 3px #999',
                }}
            >
                <Badge badgeContent={alerts.length} color="error">
                    <NotificationsNoneOutlinedIcon sx={{ fontSize: 40 }} />
                </Badge>
            </Button>

            {showAlerts && (
                <Paper
                    sx={{
                        position: 'fixed',
                        bottom: 110,
                        right: 40,
                        width: 300,
                        maxHeight: 400,
                        overflowY: 'auto',
                        boxShadow: '2px 2px 3px #999',
                    }}
                >
                    <Box sx={{ p: 2 }}>
                        <Typography variant="h6">Alertas</Typography>
                        <List>
                            {alerts.map((alert, index) => (
                                <ListItem
                                    key={index}
                                    divider
                                    sx={{
                                        border: '1px solid #ddd',
                                        borderRadius: '8px',
                                        marginBottom: '8px',
                                        backgroundColor: darkMode ? '#333' : '#fff',
                                        '&:hover': {
                                            backgroundColor: darkMode ? '#444' : '#f5f5f5',
                                        },
                                    }}
                                    onClick={() => { navigate(`/uag/${alert.aguCui}`) }}
                                >
                                    <ListItemText
                                        primary={
                                            <Typography variant="subtitle1" sx={{ fontWeight: 'bold' }}>
                                                {alert.type}
                                            </Typography>
                                        }
                                        secondary={`${alert.message} - ${new Date(alert.date).toLocaleString()}`}
                                    />
                                </ListItem>
                            ))}
                        </List>
                    </Box>
                </Paper>
            )}
        </div>
    );
}