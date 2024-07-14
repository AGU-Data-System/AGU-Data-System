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

type WeekState =
    | { type: 'loading' }
    | { type: 'error'; message: string }
    | { type: 'success'; loads: WeeklyPlanListOutputModel };

function DayCard({ day, date, plans, onCheckChange, checkedPlans, onDayChangeClick, onAddNewAguClick } : { day: string, date: string, plans: PlannedLoadOutputModel[], onCheckChange: (aguCui: string, checked: boolean) => void, checkedPlans: Set<string>, onDayChangeClick: (aguCui: string) => void, onAddNewAguClick: () => void }) {
    const [open, setOpen] = useState(false);

    return (
        <Paper elevation={3} sx={{ marginBottom: 2, padding: 2 }}>
            <Box display="flex" alignItems="center">
                <Typography variant="h6" sx={{ flex: 1 }}>{day}</Typography>
                <Typography variant="h6" sx={{ flex: 1, textAlign: 'center' }}>{date}</Typography>
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
                                    control={<Checkbox checked={checkedPlans.has(plan.aguCui)} onChange={(e) => onCheckChange(plan.aguCui, e.target.checked)} />}
                                    label={<Typography variant="h6">{plan.aguCui}</Typography>}
                                />
                                {!checkedPlans.has(plan.aguCui) && (
                                    <IconButton onClick={() => onDayChangeClick(plan.aguCui)}>
                                        <Edit />
                                    </IconButton>
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

function WeeklyPlanHeader({ weekDate } : { weekDate: string }){
    return (
        <div style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            padding: '16px',
            borderBottom: '1px solid #000000',
        }}>
            <div style={{
                flex: 1,
                textAlign: 'center',
                margin: '8px',
            }}>
                <Typography variant="h4">Plano Semanal</Typography>
                <Typography variant="body2" sx={{
                    display: 'flex',
                    justifyContent: 'flex-end',
                    marginRight: '10px'
                }}>
                    {weekDate}
                </Typography>
            </div>

            <div style={{
                textAlign: 'center',
                margin: '8px',
            }}>
                <ReturnButton />
            </div>
        </div>
    );
}

export default function WeeklyPlan() {
    const [weekState, setWeekState] = useState<WeekState>({ type: 'loading' });
    const [checkedPlans, setCheckedPlans] = useState<Set<string>>(new Set());
    const [newLoad, setNewLoad] = useState<CreatePlannedLoadInputModel>(
        {
            aguCui: '',
            date: '',
            timeOfDay: 'Morning',
            amount: '',
            isManual: 'true'
        }
    );
    const [isDialogOpen, setDialogOpen] = useState(false);
    const [selectedDay, setSelectedDay] = useState<number | null>(null);
    const [selectedAgu, setSelectedAgu] = useState<string | null>(null);
    const [isDayChangeDialogOpen, setDayChangeDialogOpen] = useState(false);

    const getWeekDates = () => {
        const today = new Date();
        const startWeekDay = new Date(today.setDate(today.getDate() - today.getDay() + 1));
        const endWeekDay = new Date(today.setDate(today.getDate() + 4));
        // values must have the format YYYY-MM-DD
        return {
            startWeekDay: startWeekDay.toISOString().split('T')[0],
            endWeekDay: endWeekDay.toISOString().split('T')[0]
        };
    }

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

    const handleCheckChange = (aguCui: string, checked: boolean) => {
        setCheckedPlans(prev => {
            const newChecked = new Set(prev);
            if (checked) {
                newChecked.add(aguCui);
            } else {
                newChecked.delete(aguCui);
            }
            return newChecked;
        });
    };

    const handleDayChange = (newDay: string) => {
        if (selectedAgu === null) return;
        setWeekState(prev => {
            if (prev.type !== 'success') return prev;
            const newLoads = prev.loads.loads.map(agu =>
                agu.aguCui === selectedAgu ? { ...agu, dayOfThePlaning: newDay } : agu
            );
            return { ...prev, loads: { ...prev.loads, loads: newLoads } };
        });
        setDayChangeDialogOpen(false);
    };

    const handleAddNewAgu = async() => {
        if (selectedDay === null) return;
        const { startWeekDay, endWeekDay } = getWeekDates();
        const newLoadWithDate = {
            ...newLoad,
            date: `${startWeekDay.split("-")[0]}-${startWeekDay.split("-")[1]}-${parseInt(startWeekDay.split("-")[2]) + selectedDay - 1}`
        };
        console.log('New Load:', newLoadWithDate);
        const addLoadResponse = await aguService.createLoad(newLoadWithDate);

        if (addLoadResponse.value instanceof Error) {
            // Show error message
            console.log('Error:', addLoadResponse.value);
        } else if (addLoadResponse.value instanceof Problem) {
            // Show error message
            console.log('Error Problem:', addLoadResponse.value);
        } else {
            const getLoadsWeeklyResponse = await aguService.getLoadsWeekly(startWeekDay, endWeekDay);

            if (getLoadsWeeklyResponse.value instanceof Error) {
                setWeekState({ type: 'error', message: getLoadsWeeklyResponse.value.message });
            } else if (getLoadsWeeklyResponse.value instanceof Problem) {
                setWeekState({ type: 'error', message: getLoadsWeeklyResponse.value.title });
            } else {
                setWeekState({ type: 'success', loads: getLoadsWeeklyResponse.value });
            }
        }
        setNewLoad({
            aguCui: '',
            date: '',
            timeOfDay: 'Morning',
            amount: '',
            isManual: 'true'
        });
        setDialogOpen(false);
    };

    const handleConfirm = () => {
        if (weekState.type !== 'success') return;
        const confirmedAgus = weekState.loads.loads.filter(agu => checkedPlans.has(agu.aguCui));
        // send confirmedAgus to the backend
        console.log('Confirmed AGUs:', confirmedAgus);
    };

    const handleOpenDialog = (day: number) => {
        setSelectedDay(day);
        setDialogOpen(true);
    };

    const handleCloseDialog = () => {
        setDialogOpen(false);
    };

    const handleOpenDayChangeDialog = (aguCui: string) => {
        setSelectedAgu(aguCui);
        setDayChangeDialogOpen(true);
    };

    const handleCloseDayChangeDialog = () => {
        setDayChangeDialogOpen(false);
    };

    const daysOfWeek = ["Segunda", "Terça", "Quarta", "Quinta", "Sexta"];

    if (weekState.type === 'loading') return (
        <div>
            <WeeklyPlanHeader weekDate="Loading..." />
        </div>
    );

    if (weekState.type === 'error') return (
        <div>
            <WeeklyPlanHeader weekDate="Error" />
            <Typography variant="h6" color="error">
                {weekState.message}
            </Typography>
        </div>
    );

    const { startWeekDay, endWeekDay, loads } = weekState.loads;

    return (
        <div>
            <WeeklyPlanHeader weekDate={`${startWeekDay} - ${endWeekDay}`} />
            {daysOfWeek.map((day, index) => {
                const dayPlans = loads.filter(plan => parseInt(plan.date.split("-")[2]) === parseInt(startWeekDay.split("-")[2]) + index) || [];
                const dayOfWeek = parseInt(startWeekDay.split("-")[2]) + index;
                return (
                    <DayCard
                        key={index}
                        day={day}
                        date={dayOfWeek.toString()}
                        plans={dayPlans}
                        onCheckChange={handleCheckChange}
                        checkedPlans={checkedPlans}
                        onDayChangeClick={handleOpenDayChangeDialog}
                        onAddNewAguClick={() => handleOpenDialog(index + 1)}
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
                        sx={{ marginBottom: 2 }}
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
                    <Button onClick={handleAddNewAgu} color="primary">
                        Add
                    </Button>
                </DialogActions>
            </Dialog>
            <Dialog open={isDayChangeDialogOpen} onClose={handleCloseDayChangeDialog}>
                <DialogTitle>Mudar o dia do plano da UAG</DialogTitle>
                <DialogContent>
                    <FormControl fullWidth sx={{ marginBottom: 2 }}>
                        <InputLabel>Day of the Planning</InputLabel>
                        <Select
                            value={newLoad.date}
                            onChange={(e) => setNewLoad({ ...newLoad, date: e.target.value })}
                        >
                            {daysOfWeek.map((day, idx) => (
                                <MenuItem key={idx} value={(idx + 1).toString()}>{day}</MenuItem>  // Use string value
                            ))}
                        </Select>
                    </FormControl>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDayChangeDialog} color="secondary">
                        Cancel
                    </Button>
                    <Button onClick={() => handleDayChange(newLoad.date)} color="primary">
                        Confirm
                    </Button>
                </DialogActions>
            </Dialog>
            <div style={{
                display: "grid",
                placeItems: "center"
            }}>
                <ConfirmPlannedAgusButton handleClick={handleConfirm} />
            </div>
        </div>
    );
}