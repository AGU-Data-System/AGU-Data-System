import * as React from 'react';
import { Button, Badge, Box, Typography, Paper, List, ListItem, ListItemText, IconButton, CircularProgress } from '@mui/material';
import NotificationsNoneOutlinedIcon from '@mui/icons-material/NotificationsNoneOutlined';
import CheckIcon from '@mui/icons-material/Check';
import { useEffect, useState } from 'react';
import { AlertsOutputModel } from "../../services/agu/models/alertsOutputModel";
import { useNavigate } from "react-router-dom";
import { aguService } from "../../services/agu/aguService";
import { Problem } from "../../utils/Problem";

type AlertsState =
    | { type: 'loading' }
    | { type: 'error'; message: string }
    | { type: 'success'; alerts: AlertsOutputModel[] };

export default function FloatingAlerts({ darkMode }: { darkMode: boolean }) {
    const [alerts, setAlerts] = useState<AlertsState>({ type: 'loading' });
    const [showAlerts, setShowAlerts] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchAlerts = async () => {
            const alerts = await aguService.getAlerts();

            if (alerts.value instanceof Error) {
                setAlerts({ type: 'error', message: alerts.value.message });
            } else if (alerts.value instanceof Problem) {
                setAlerts({ type: 'error', message: alerts.value.title });
            } else {
                setAlerts({ type: 'success', alerts: alerts.value.alerts });
            }
        }

        fetchAlerts();
    }, []);

    const toggleShowAlerts = () => {
        setShowAlerts(prev => !prev);
    };

    const handleAlertResolve = async (alert: AlertsOutputModel) => {
        const result = await aguService.updateAlertStatus(alert.id);

        if (result.value instanceof Error) {
            setAlerts({ type: 'error', message: result.value.message });
        } else if (result.value instanceof Problem) {
            setAlerts({ type: 'error', message: result.value.title });
        } else {
            setAlerts({ type: 'success', alerts: result.value.alerts });
        }
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
                <Badge badgeContent={alerts.type === 'success' ? alerts.alerts.length : 0} color="error">
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
                        backgroundColor: darkMode ? '#333' : '#fff',
                    }}
                >
                    <Box sx={{ p: 2 }}>
                        <Typography variant="h6">Alertas</Typography>
                        {alerts.type === 'loading' && (
                            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', flexDirection: 'column', height: '100px' }}>
                                <CircularProgress sx={{ color: 'rgb(255, 165, 0)' }} />
                                <Typography variant="h6" sx={{ marginTop: 1 }}>Loading...</Typography>
                            </Box>
                        )}
                        {alerts.type === 'error' && (
                            <Typography variant="h6" color="error">
                                {alerts.message}
                            </Typography>
                        )}
                        {alerts.type === 'success' && (
                            <List>
                                {alerts.alerts.map((alert, index) => (
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
                                    >
                                        <ListItemText
                                            primary={
                                                <Typography variant="subtitle1" sx={{ fontWeight: 'bold' }}>
                                                    {alert.title}
                                                </Typography>
                                            }
                                            secondary={`${alert.message} - ${new Date(alert.timestamp).toLocaleString()}`}
                                            onClick={() => { navigate(`/uag/${alert.agu}`) }}
                                        />
                                        <IconButton
                                            edge="end"
                                            aria-label="resolve"
                                            onClick={() => handleAlertResolve(alert)}
                                        >
                                            <CheckIcon sx={{ color: 'rgb(255, 165, 0)' }} />
                                        </IconButton>
                                    </ListItem>
                                ))}
                            </List>
                        )}
                    </Box>
                </Paper>
            )}
        </div>
    );
}