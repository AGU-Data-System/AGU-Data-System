import * as React from "react";
import { Button, Card, CardContent, Divider, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Snackbar, Alert } from "@mui/material";
import Typography from "@mui/material/Typography";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { aguService } from "../../services/agu/aguService";
import { Problem } from "../../utils/Problem";

type AlertSeverity = 'error' | 'warning' | 'info' | 'success';

export function ControlMenuLeft() {
    const navigate = useNavigate();
    const [open, setOpen] = useState(false);
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState('');
    const [snackbarSeverity, setSnackbarSeverity] = useState<AlertSeverity>('success');

    const handleTreinarClick = () => {
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    const handleSnackbarClose = () => {
        setSnackbarOpen(false);
    };

    const handleConfirm = async () => {
        setOpen(false);
        const getLoadsWeeklyResponse = await aguService.trainAgus();

        if (getLoadsWeeklyResponse.value instanceof Error) {
            setSnackbarMessage(getLoadsWeeklyResponse.value.message);
            setSnackbarSeverity('error');
        } else if (getLoadsWeeklyResponse.value instanceof Problem) {
            setSnackbarMessage(getLoadsWeeklyResponse.value.title);
            setSnackbarSeverity('error');
        } else {
            setSnackbarMessage('Treino realizado.');
            setSnackbarSeverity('success');
        }
        setSnackbarOpen(true);
    };

    return (
        <Card>
            <CardContent>
                <Typography variant="h5" component="div">
                    Menu de admistração
                </Typography>
                <br />
                <div
                    style={{
                        display: "flex",
                        flexDirection: "row",
                        flexWrap: "wrap",
                        justifyContent: "flex-start",
                    }}
                >
                    <Button variant="contained" sx={{
                        margin: '8px',
                        backgroundColor: 'rgb(255, 165, 0)',
                        height: '100%',
                        color: 'black',
                        '&:hover': {
                            backgroundColor: 'rgba(255,165,0,0.49)',
                        },
                    }} onClick={() => {
                        navigate('/uag/create')
                    }}>
                        Adicionar UAG
                    </Button>
                    <Button variant="contained" sx={{
                        margin: '8px',
                        backgroundColor: 'rgb(255, 165, 0)',
                        height: '100%',
                        color: 'black',
                        '&:hover': {
                            backgroundColor: 'rgba(255,165,0,0.49)',
                        },
                    }} onClick={handleTreinarClick}>
                        Treinar UAGs
                    </Button>
                </div>
                <Divider orientation="horizontal" flexItem sx={{ marginTop: 2, marginBottom: 2 }} />
                <Typography variant="h5" component="div">
                    Plano Semanal Ideal
                </Typography>
                <br />
                <div
                    style={{
                        display: "flex",
                        flexDirection: "row",
                        flexWrap: "wrap",
                        justifyContent: "flex-start",
                    }}
                >
                    <Button variant="contained" sx={{
                        margin: '8px',
                        backgroundColor: 'rgb(255, 165, 0)',
                        height: '100%',
                        color: 'black',
                        '&:hover': {
                            backgroundColor: 'rgba(255,165,0,0.49)',
                        },
                    }} onClick={() => {
                        navigate('/weekly-plan')
                    }}>
                        Plano Semanal
                    </Button>
                </div>
            </CardContent>

            <Dialog open={open} onClose={handleClose}>
                <DialogTitle>Confirmar Treinamento</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Você tem certeza de que deseja treinar todas as UAGs?
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose} color="primary">
                        Cancelar
                    </Button>
                    <Button onClick={handleConfirm} color="primary" autoFocus>
                        Confirmar
                    </Button>
                </DialogActions>
            </Dialog>

            <Snackbar open={snackbarOpen} autoHideDuration={6000} onClose={handleSnackbarClose}>
                <Alert onClose={handleSnackbarClose} severity={snackbarSeverity} sx={{ width: '100%' }}>
                    {snackbarMessage}
                </Alert>
            </Snackbar>
        </Card>
    );
}

export function ControlMenuRight({ setFilter } : { setFilter: (filter: string) => void }) {
    const [activeButton, setActiveButton] = useState<string>("");

    const handleClick = (filter: string) => {
        if (activeButton === filter) {
            setFilter("");
            setActiveButton("");
            return;
        }
        setFilter(filter);
        setActiveButton(filter);
    }

    return (
        <Card>
            <CardContent>
                <Typography variant="h5" component="div">
                    Menu de Acessibilidade
                </Typography>
                <br/>
                <div
                    style={{
                        display: "flex",
                        flexDirection: "row",
                        flexWrap: "wrap",
                        justifyContent: "flex-start",
                    }}
                >
                    <Button
                        variant="contained"
                        sx={{
                            margin: '8px',
                            backgroundColor: activeButton === "norte" ? 'rgba(255,165,0,0.49)' : 'rgb(255, 165, 0)',
                            height: '100%',
                            color: 'black',
                            '&:hover': {
                                backgroundColor: 'rgba(255,165,0,0.49)',
                            },
                        }}
                        onClick={() => handleClick("norte")}
                    >
                        Norte
                    </Button>
                    <Button
                        variant="contained"
                        sx={{
                            margin: '8px',
                            backgroundColor: activeButton === "centro" ? 'rgba(255,165,0,0.49)' : 'rgb(255, 165, 0)',
                            height: '100%',
                            color: 'black',
                            '&:hover': {
                                backgroundColor: 'rgba(255,165,0,0.49)',
                            },
                        }}
                        onClick={() => handleClick("centro")}
                    >
                        Centro
                    </Button>
                    <Button
                        variant="contained"
                        sx={{
                            margin: '8px',
                            backgroundColor: activeButton === "sul" ? 'rgba(255,165,0,0.49)' : 'rgb(255, 165, 0)',
                            height: '100%',
                            color: 'black',
                            '&:hover': {
                                backgroundColor: 'rgba(255,165,0,0.49)',
                            },
                        }}
                        onClick={() => handleClick("sul")}
                    >
                        Sul
                    </Button>
                </div>
                <div
                    style={{
                        display: "flex",
                        flexDirection: "row",
                        flexWrap: "wrap",
                        justifyContent: "flex-start",
                    }}
                >
                    <Button
                        variant="contained"
                        sx={{
                            margin: '8px',
                            backgroundColor: activeButton === "sng" ? 'rgba(255,165,0,0.49)' : 'rgb(255, 165, 0)',
                            height: '100%',
                            color: 'black',
                            '&:hover': {
                                backgroundColor: 'rgba(255,165,0,0.49)',
                            },
                        }}
                        onClick={() => handleClick("sng")}
                    >
                        Sonorgás
                    </Button>
                    <Button
                        variant="contained"
                        sx={{
                            margin: '8px',
                            backgroundColor: activeButton === "tgg" ? 'rgba(255,165,0,0.49)' : 'rgb(255, 165, 0)',
                            height: '100%',
                            color: 'black',
                            '&:hover': {
                                backgroundColor: 'rgba(255,165,0,0.49)',
                            },
                        }}
                        onClick={() => handleClick("tgg")}
                    >
                        Tagusgás
                    </Button>
                    <Button
                        variant="contained"
                        sx={{
                            margin: '8px',
                            backgroundColor: activeButton === "dur" ? 'rgba(255,165,0,0.49)' : 'rgb(255, 165, 0)',
                            height: '100%',
                            color: 'black',
                            '&:hover': {
                                backgroundColor: 'rgba(255,165,0,0.49)',
                            },
                        }}
                        onClick={() => handleClick("dur")}
                    >
                        Duriensegás
                    </Button>
                </div>
                <div
                    style={{
                        display: "flex",
                        flexDirection: "row",
                        flexWrap: "wrap",
                        justifyContent: "flex-start",
                    }}
                >
                    <Button
                        variant="contained"
                        sx={{
                            margin: '8px',
                            backgroundColor: activeButton === "inactive" ? 'rgba(255,165,0,0.49)' : 'rgb(255, 165, 0)',
                            height: '100%',
                            color: 'black',
                            '&:hover': {
                                backgroundColor: 'rgba(255,165,0,0.49)',
                            },
                        }}
                        onClick={() => handleClick("inactive")}
                    >
                        UAGs Inativas
                    </Button>
                </div>
                    <br/>
                    <Divider orientation="horizontal" flexItem/>
            </CardContent>
        </Card>
);
}