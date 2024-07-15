import * as React from 'react';
import { useEffect, useState } from 'react';
import { Box, Paper, Typography, IconButton, Collapse, Button, TextField, FormControl, InputLabel, Select, MenuItem, Checkbox, FormControlLabel, Dialog, DialogActions, DialogContent, DialogTitle } from '@mui/material';
import { ExpandMore, ExpandLess, Add, Edit } from '@mui/icons-material';
import { ConfirmPlannedAgusButton, ReturnButton } from "../Layouts/Buttons";
import {
    WeeklyPlanListOutputModel,
    PlannedLoadOutputModel,
    CreatePlannedLoadInputModel
} from "../../services/agu/models/weeklyPlanOutputModel";
import { aguService } from "../../services/agu/aguService";
import { Problem } from "../../utils/Problem";
import DeleteIcon from '@mui/icons-material/Delete';
import { Snackbar, Alert } from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';

type WeekState =
    | { type: 'loading' }
    | { type: 'error'; message: string }
    | { type: 'success'; loads: WeeklyPlanListOutputModel };

function DayCard({
                     day,
                     date,
                     plans,
                     onCheckChange,
                     checkedPlans,
                     onDayChangeClick,
                     onAddNewAguClick,
                     onDeleteLoadClick
                 } : {
    day: string,
    date: string,
    plans: PlannedLoadOutputModel[],
    onCheckChange: (loadId: number, checked: boolean) => void,
    checkedPlans: Set<number>,
    onDayChangeClick: (loadId: number) => void,
    onAddNewAguClick: () => void,
    onDeleteLoadClick: (loadId: number) => void
}) {
    const [open, setOpen] = useState(false);

    return (
        <Paper elevation={3} sx={{ marginBottom: 2, padding: 2 }}>
            <Box display="flex" alignItems="center">
                <Typography variant="h6" sx={{ flex: 1 }}>{day}</Typography>
                <Typography variant="h6" sx={{ flex: 1, textAlign: 'center' }}>Dia: {date}</Typography>
                <Typography variant="body1" sx={{ flex: 1, textAlign: 'right' }}>
                    Loads planeadas: {plans.length}
                </Typography>
                <Box sx={{ flex: 1, display: 'flex', justifyContent: 'flex-end' }}>
                    <IconButton onClick={() => setOpen(!open)}>
                        {plans.length > 0 ? (open ? <ExpandLess /> : <ExpandMore />) : ""}
                    </IconButton>
                    <IconButton onClick={onAddNewAguClick}>
                        <Add />
                    </IconButton>
                </Box>
            </Box>
            {plans.length > 0 && (
                <Collapse in={open}>
                    <Box sx={{ marginTop: 2, padding: 2, border: '1px solid black' }}>
                        {plans.map((plan, index) => (
                            <Box key={index} sx={{ marginBottom: 2 }}>
                                <FormControlLabel
                                    control={<Checkbox checked={checkedPlans.has(plan.loadId) || plan.isConfirmed === 'true'} onChange={(e) => onCheckChange(plan.loadId, e.target.checked)} />}
                                    label={<Typography variant="h6">{plan.locationName} ({plan.aguCui}) </Typography>}
                                />
                                {!checkedPlans.has(plan.loadId) && plan.isConfirmed === 'false' && (
                                    <>
                                        <IconButton onClick={() => onDayChangeClick(plan.loadId)}>
                                            <Edit />
                                        </IconButton>
                                        <IconButton onClick={() => onDeleteLoadClick(plan.loadId)}>
                                            <DeleteIcon />
                                        </IconButton>
                                    </>
                                )}
                                <Typography variant="body1">Parte do dia: {plan.timeOfDay == 'MORNING' ? 'Manhã' : 'Tarde'}</Typography>
                                <Typography variant="body1">Quantidade da carga: {plan.amount}</Typography>
                                <Typography variant="body1">{plan.isManual ? 'Manual' : 'Automática'}</Typography>
                            </Box>
                        ))}
                    </Box>
                </Collapse>
            )}
        </Paper>
    );
}

function WeeklyPlanHeader({ weekDate, onWeekChange }: { weekDate: string, onWeekChange: (start: string, end: string) => void }) {
    const [startDate, setStartDate] = useState(new Date());
    const [endDate, setEndDate] = useState(new Date(new Date().setDate(new Date().getDate() + 4)));
    const [isDialogOpen, setDialogOpen] = useState(false);

    const handleClickWeekChange = () => {
        setDialogOpen(true);
    };

    const handleConfirmWeekChange = () => {
        onWeekChange(startDate.toISOString().split('T')[0], endDate.toISOString().split('T')[0]);
        setDialogOpen(false);
    };

    const handleCloseDialog = () => {
        setDialogOpen(false);
    };

    const handleStartDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newStartDate = new Date(e.target.value);
        setStartDate(newStartDate);
        setEndDate(new Date(newStartDate.getTime() + 4 * 24 * 60 * 60 * 1000));
    };

    return (
        <>
            <div style={{
                display: 'flex',
                alignItems: 'center',
                padding: '16px',
                borderBottom: '1px solid #000000',
            }}>
                <div style={{
                    flex: 1,
                    textAlign: 'center',
                    margin: '8px',
                }}>
                    <Typography variant="h4">Plano Semanal</Typography>
                    <div style={{
                        display: 'flex',
                        flexDirection: 'row',
                        alignItems: 'center',
                        justifyContent: 'center',
                    }}>
                        <Typography variant="body2">{weekDate}</Typography>
                        <EditIcon onClick={handleClickWeekChange} sx={{ marginLeft: 1 }}/>
                    </div>
                </div>
                <div style={{
                    textAlign: 'center',
                    margin: '8px',
                }}>
                    <ReturnButton />
                </div>
            </div>

            <Dialog open={isDialogOpen} onClose={handleCloseDialog}>
                <DialogTitle>Alterar Semana</DialogTitle>
                <DialogContent>
                    <TextField
                        label="Início da Semana"
                        type="date"
                        value={startDate.toISOString().split('T')[0]}
                        onChange={handleStartDateChange}
                        fullWidth
                        sx={{ marginBottom: 2, marginTop: 2 }}
                    />
                    <TextField
                        label="Fim da Semana"
                        type="date"
                        value={endDate.toISOString().split('T')[0]}
                        disabled={true}
                        fullWidth
                        sx={{ marginBottom: 2 }}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDialog} color="secondary">
                        Cancelar
                    </Button>
                    <Button onClick={handleConfirmWeekChange} color="primary">
                        Confirmar
                    </Button>
                </DialogActions>
            </Dialog>
        </>
    );
}

export default function WeeklyPlan() {
    const [weekState, setWeekState] = useState<WeekState>({ type: 'loading' });
    const [checkedPlans, setCheckedPlans] = useState<Set<number>>(new Set());
    const [newLoad, setNewLoad] = useState<CreatePlannedLoadInputModel>({
        aguCui: '',
        date: '',
        timeOfDay: 'Morning',
        amount: '',
        isManual: 'true'
    });
    const [isDialogOpen, setDialogOpen] = useState(false);
    const [selectedDay, setSelectedDay] = useState<number | null>(null);
    const [selectedLoadId, setSelectedLoadId] = useState<number | null>(null);
    const [isDayChangeDialogOpen, setDayChangeDialogOpen] = useState(false);
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState('');
    const [snackbarSeverity, setSnackbarSeverity] = useState<'success' | 'error'>('success');
    const [isDeleteConfirmDialogOpen, setDeleteConfirmDialogOpen] = useState(false);

    const getWeekDates = () => {
        const today = new Date();
        const dayOfWeek = today.getDay();
        let startOfWeek;

        if (dayOfWeek === 6 || dayOfWeek === 0) { // If today is Saturday (6) or Sunday (0)
            // Calculate days until next Monday
            const daysUntilNextMonday = ((7 - dayOfWeek + 1) % 7) + 1;
            startOfWeek = new Date(today);
            startOfWeek.setDate(today.getDate() + daysUntilNextMonday);
        } else {
            // Calculate the start of this week (Monday)
            startOfWeek = new Date(today);
            startOfWeek.setDate(today.getDate() - dayOfWeek + 1);
        }

        // Calculate the end of the week (Friday)
        const endOfWeek = new Date(startOfWeek);
        endOfWeek.setDate(startOfWeek.getDate() + 4);

        return {
            startWeekDay: startOfWeek.toISOString().split('T')[0],
            endWeekDay: endOfWeek.toISOString().split('T')[0]
        };
    };

    useEffect(() => {
        const fetchWeeklyPlan = async () => {
            const { startWeekDay, endWeekDay } = getWeekDates();
            const getLoadsWeeklyResponse = await aguService.getLoadsWeekly(startWeekDay, endWeekDay);

            if (getLoadsWeeklyResponse.value instanceof Error) {
                setWeekState({ type: 'error', message: getLoadsWeeklyResponse.value.message });
            } else if (getLoadsWeeklyResponse.value instanceof Problem) {
                setWeekState({ type: 'error', message: getLoadsWeeklyResponse.value.title });
            } else {
                setWeekState({ type: 'success', loads: getLoadsWeeklyResponse.value });
            }
        };
        fetchWeeklyPlan();
    }, []);

    const handleCheckChange = (loadId: number, checked: boolean) => {
        setCheckedPlans(prev => {
            const newChecked = new Set(prev);
            if (checked) {
                newChecked.add(loadId);
            } else {
                newChecked.delete(loadId);
            }
            return newChecked;
        });
    };

    const handleDayChange = async (newDay: string) => {
        if (selectedLoadId === null) return;
        const { startWeekDay, endWeekDay } = getWeekDates();
        const newDate = `${startWeekDay.split("-")[0]}-${startWeekDay.split("-")[1]}-${parseInt(startWeekDay.split("-")[2]) + parseInt(newDay) - 1}`;
        const changeDayResponse = await aguService.changeLoadDay(selectedLoadId, newDate);
        if (changeDayResponse.value instanceof Error) {
            setSnackbarMessage(changeDayResponse.value.message);
            setSnackbarSeverity('error');
        } else if (changeDayResponse.value instanceof Problem) {
            setSnackbarMessage(changeDayResponse.value.title);
            setSnackbarSeverity('error');
        } else {
            const getLoadsWeeklyResponse = await aguService.getLoadsWeekly(startWeekDay, endWeekDay);
            if (getLoadsWeeklyResponse.value instanceof Error) {
                setWeekState({ type: 'error', message: getLoadsWeeklyResponse.value.message });
            } else if (getLoadsWeeklyResponse.value instanceof Problem) {
                setWeekState({ type: 'error', message: getLoadsWeeklyResponse.value.title });
            } else {
                setWeekState({ type: 'success', loads: getLoadsWeeklyResponse.value });
            }
            setSnackbarMessage('Day changed successfully');
            setSnackbarSeverity('success');
        }
        setDayChangeDialogOpen(false);
        setSnackbarOpen(true);
    };

    const handleAddNewAgu = async (startWeekDay: string) => {
        if (selectedDay === null) return;

        const newLoadWithDate = {
            ...newLoad,
            date: `${startWeekDay.split("-")[0]}-${startWeekDay.split("-")[1]}-${parseInt(startWeekDay.split("-")[2]) + selectedDay - 1}`
        };

        const addLoadResponse = await aguService.createLoad(newLoadWithDate);

        if (addLoadResponse.value instanceof Error) {
            setSnackbarMessage(addLoadResponse.value.message);
            setSnackbarSeverity('error');
        } else if (addLoadResponse.value instanceof Problem) {
            setSnackbarMessage(addLoadResponse.value.title);
            setSnackbarSeverity('error');
        } else {
            const getLoadsWeeklyResponse = await aguService.getLoadsWeekly(startWeekDay, endWeekDay);
            if (getLoadsWeeklyResponse.value instanceof Error) {
                setWeekState({ type: 'error', message: getLoadsWeeklyResponse.value.message });
            } else if (getLoadsWeeklyResponse.value instanceof Problem) {
                setWeekState({ type: 'error', message: getLoadsWeeklyResponse.value.title });
            } else {
                setWeekState({ type: 'success', loads: getLoadsWeeklyResponse.value });
            }
            setSnackbarMessage('Load added successfully');
            setSnackbarSeverity('success');
        }
        setNewLoad({
            aguCui: '',
            date: '',
            timeOfDay: 'Morning',
            amount: '',
            isManual: 'true'
        });
        setDialogOpen(false);
        setSnackbarOpen(true);
    };

    const handleDeleteLoad = async () => {
        if (selectedLoadId === null) return;
        const deleteResponse = await aguService.deleteLoad(selectedLoadId);
        const { startWeekDay, endWeekDay } = getWeekDates();

        if (deleteResponse.value instanceof Error) {
            setSnackbarMessage(deleteResponse.value.message);
            setSnackbarSeverity('error');
        } else if (deleteResponse.value instanceof Problem) {
            setSnackbarMessage(deleteResponse.value.title);
            setSnackbarSeverity('error');
        } else {
            const getLoadsWeeklyResponse = await aguService.getLoadsWeekly(startWeekDay, endWeekDay);
            if (getLoadsWeeklyResponse.value instanceof Error) {
                setWeekState({ type: 'error', message: getLoadsWeeklyResponse.value.message });
            } else if (getLoadsWeeklyResponse.value instanceof Problem) {
                setWeekState({ type: 'error', message: getLoadsWeeklyResponse.value.title });
            } else {
                setWeekState({ type: 'success', loads: getLoadsWeeklyResponse.value });
            }
            setSnackbarMessage('Load deleted successfully');
            setSnackbarSeverity('success');
        }
        setDeleteConfirmDialogOpen(false);
        setSnackbarOpen(true);
    };

    const handleConfirm = () => {
        if (weekState.type !== 'success') return;
        const confirmedAgus = weekState.loads.loads.filter(agu => checkedPlans.has(agu.loadId));
        confirmedAgus.forEach(async agu => {
            const confirmResponse = await aguService.confirmLoads(agu.loadId);
            const { startWeekDay, endWeekDay } = getWeekDates();

            if (confirmResponse.value instanceof Error) {
                setSnackbarMessage(confirmResponse.value.message);
                setSnackbarSeverity('error');
            } else if (confirmResponse.value instanceof Problem) {
                setSnackbarMessage(confirmResponse.value.title);
                setSnackbarSeverity('error');
            } else {
                const getLoadsWeeklyResponse = await aguService.getLoadsWeekly(startWeekDay, endWeekDay);
                if (getLoadsWeeklyResponse.value instanceof Error) {
                    setWeekState({ type: 'error', message: getLoadsWeeklyResponse.value.message });
                } else if (getLoadsWeeklyResponse.value instanceof Problem) {
                    setWeekState({ type: 'error', message: getLoadsWeeklyResponse.value.title });
                } else {
                    setWeekState({ type: 'success', loads: getLoadsWeeklyResponse.value });
                }
                setSnackbarMessage('Loads confirmed successfully');
                setSnackbarSeverity('success');
            }
            setSnackbarOpen(true);
        });
    };

    const handleOpenDialog = (day: number) => {
        setSelectedDay(day);
        setDialogOpen(true);
    };

    const handleCloseDialog = () => {
        setDialogOpen(false);
    };

    const handleOpenDayChangeDialog = (loadId: number) => {
        setSelectedLoadId(loadId);
        setDayChangeDialogOpen(true);
    };

    const handleCloseDayChangeDialog = () => {
        setDayChangeDialogOpen(false);
    };

    const handleSnackbarClose = () => {
        setSnackbarOpen(false);
    };

    const handleOpenDeleteConfirmDialog = (loadId: number) => {
        setSelectedLoadId(loadId);
        setDeleteConfirmDialogOpen(true);
    };

    const handleCloseDeleteConfirmDialog = () => {
        setDeleteConfirmDialogOpen(false);
    };

    const handleWeekChange = async(start: string, end: string) => {
        const getLoadsWeeklyResponse = await aguService.getLoadsWeekly(start, end);

        if (getLoadsWeeklyResponse.value instanceof Error) {
            setWeekState({ type: 'error', message: getLoadsWeeklyResponse.value.message });
        } else if (getLoadsWeeklyResponse.value instanceof Problem) {
            setWeekState({ type: 'error', message: getLoadsWeeklyResponse.value.title ?? 'Error' });
        } else {
            setWeekState({ type: 'success', loads: getLoadsWeeklyResponse.value });
        }
    };

    const dayOfWeek = (startDate: string, index: number) => {
        const dateParts = startDate.split("-");
        const year = parseInt(dateParts[0], 10);
        const month = parseInt(dateParts[1], 10);
        let day = parseInt(dateParts[2], 10) + index;

        // Get the number of days in the current month
        const daysInMonth = new Date(year, month, 0).getDate();

        // Adjust the day if it exceeds the days in the month
        if (day > daysInMonth) {
            day -= daysInMonth;
        }

        return day;
    }

    const daysOfWeek = ["Segunda", "Terça", "Quarta", "Quinta", "Sexta"];

    if (weekState.type === 'loading') return (
        <div>
            <WeeklyPlanHeader weekDate="Loading..." onWeekChange={()=>{}}/>
        </div>
    );

    if (weekState.type === 'error') return (
        <div>
            <WeeklyPlanHeader weekDate="Error" onWeekChange={()=>{}}/>
            <Typography variant="h6" color="error">
                {weekState.message}
            </Typography>
        </div>
    );

    const { startWeekDay, endWeekDay, loads } = weekState.loads;

    return (
        <div>
            <WeeklyPlanHeader weekDate={`${startWeekDay} - ${endWeekDay}`} onWeekChange={handleWeekChange} />
            {daysOfWeek.map((day, index) => {
                const dayPlans = loads.filter(plan => parseInt(plan.date.split("-")[2]) === parseInt(startWeekDay.split("-")[2]) + index) || [];
                return (
                    <DayCard
                        key={index}
                        day={day}
                        date={dayOfWeek(startWeekDay, index).toString()}
                        plans={dayPlans}
                        onCheckChange={handleCheckChange}
                        checkedPlans={checkedPlans}
                        onDayChangeClick={handleOpenDayChangeDialog}
                        onAddNewAguClick={() => handleOpenDialog(index + 1)}
                        onDeleteLoadClick={handleOpenDeleteConfirmDialog} // Change here
                    />
                );
            })}
            <Dialog open={isDialogOpen} onClose={handleCloseDialog}>
                <DialogTitle>Adicionar nova load</DialogTitle>
                <DialogContent>
                    <TextField
                        label="UAG CUI"
                        value={newLoad.aguCui}
                        onChange={(e) => setNewLoad({ ...newLoad, aguCui: e.target.value })}
                        fullWidth
                        sx={{ marginBottom: 2, marginTop: 2}}
                    />
                    <TextField
                        label="Parte do dia"
                        type="string"
                        value={newLoad?.timeOfDay}
                        onChange={(e) => setNewLoad({ ...newLoad, timeOfDay: e.target.value })}
                        fullWidth
                        sx={{ marginBottom: 2 }}
                    />
                    <TextField
                        label="Quantidade da carga"
                        type="number"
                        value={newLoad?.amount}
                        onChange={(e) => setNewLoad({ ...newLoad, amount: e.target.value })}
                        fullWidth
                        sx={{ marginBottom: 2 }}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDialog} color="secondary">
                        Cancel
                    </Button>
                    <Button onClick={() => handleAddNewAgu(startWeekDay)} color="primary">
                        Add
                    </Button>
                </DialogActions>
            </Dialog>
            <Dialog open={isDayChangeDialogOpen} onClose={handleCloseDayChangeDialog}>
                <DialogTitle>Mudar o dia do plano da UAG</DialogTitle>
                <DialogContent>
                    <FormControl fullWidth sx={{ marginBottom: 2, marginTop: 2 }}>
                        <InputLabel>Dia do planeamento</InputLabel>
                        <Select
                            value={newLoad.date}
                            onChange={(e) => setNewLoad({ ...newLoad, date: e.target.value })}
                        >
                            {daysOfWeek.map((day, idx) => (
                                <MenuItem key={idx} value={(idx + 1).toString()}>{day}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDayChangeDialog} color="secondary">
                        Cancelar
                    </Button>
                    <Button onClick={() => handleDayChange(newLoad.date)} color="primary">
                        Confirmar
                    </Button>
                </DialogActions>
            </Dialog>
            <Dialog open={isDeleteConfirmDialogOpen} onClose={handleCloseDeleteConfirmDialog}>
                <DialogTitle>Confirmar remoção</DialogTitle>
                <DialogContent>
                    <Typography>Tem a certeza que pretende remover esta load?</Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDeleteConfirmDialog} color="secondary">
                        Cancelar
                    </Button>
                    <Button onClick={handleDeleteLoad} color="primary">
                        Apagar
                    </Button>
                </DialogActions>
            </Dialog>
            <div style={{ display: "grid", placeItems: "center" }}>
                <ConfirmPlannedAgusButton handleClick={handleConfirm} />
            </div>
            <Snackbar open={snackbarOpen} autoHideDuration={6000} onClose={handleSnackbarClose}>
                <Alert onClose={handleSnackbarClose} severity={snackbarSeverity}>
                    {snackbarMessage}
                </Alert>
            </Snackbar>
        </div>
    );
}