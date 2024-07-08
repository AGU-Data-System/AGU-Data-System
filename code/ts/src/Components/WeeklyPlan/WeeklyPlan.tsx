import * as React from 'react';
import { useEffect, useState } from 'react';
import { Box, Paper, Typography, IconButton, Collapse, Button, TextField, FormControl, InputLabel, Select, MenuItem, Checkbox, FormControlLabel, Dialog, DialogActions, DialogContent, DialogTitle } from '@mui/material';
import { ExpandMore, ExpandLess, Add, Edit } from '@mui/icons-material';
import {ConfirmPlannedAgusButton, ReturnButton} from "../Layouts/Buttons";
import { WeeklyPlanOutputModel } from "../../services/agu/models/weeklyPlanOutputModel";

function DayCard({ day, date, plans, onCheckChange, checkedPlans, onDayChangeClick, onAddNewAguClick } : { day: string, date: string, plans: Array<{aguCui: number, aguName: string, currentGasLevel: number, plannedGasLevel: number}>, onCheckChange: (aguCui: number, checked: boolean) => void, checkedPlans: Set<number>, onDayChangeClick: (aguCui: number) => void, onAddNewAguClick: () => void }) {
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
                                    label={<Typography variant="h6">{plan.aguName}</Typography>}
                                />
                                {!checkedPlans.has(plan.aguCui) && (
                                    <IconButton onClick={() => onDayChangeClick(plan.aguCui)}>
                                        <Edit />
                                    </IconButton>
                                )}
                                <Typography>Current Gas Level: {plan.currentGasLevel}</Typography>
                                <Typography>Planned Gas Level: {plan.plannedGasLevel}</Typography>
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
    const [aguList, setAguList] = useState<WeeklyPlanOutputModel | null>(null);
    const [checkedPlans, setCheckedPlans] = useState<Set<number>>(new Set());
    const [newAgu, setNewAgu] = useState({ aguName: '', currentGasLevel: 0, plannedGasLevel: 0, dayOfThePlaning: 1 });
    const [isDialogOpen, setDialogOpen] = useState(false);
    const [selectedDay, setSelectedDay] = useState<number | null>(null);
    const [selectedAgu, setSelectedAgu] = useState<number | null>(null);
    const [isDayChangeDialogOpen, setDayChangeDialogOpen] = useState(false);

    useEffect(() => {
        const fetchWeeklyPlan = async () => {
            setAguList({
                weekStartDay: "24/06/2024",
                weekEndDay: "28/06/2024",
                plannedAgus: [
                    {
                        aguCui: 1,
                        aguName: "AGU 1",
                        dayOfThePlaning: 1,
                        currentGasLevel: 25,
                        plannedGasLevel: 75
                    },
                    {
                        aguCui: 2,
                        aguName: "AGU 2",
                        dayOfThePlaning: 1,
                        currentGasLevel: 20,
                        plannedGasLevel: 80
                    },
                    {
                        aguCui: 3,
                        aguName: "AGU 3",
                        dayOfThePlaning: 2,
                        currentGasLevel: 20,
                        plannedGasLevel: 80
                    },
                ]
            });
        }
        fetchWeeklyPlan();
    }, []);

    const handleCheckChange = (aguCui: number, checked: boolean) => {
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

    const handleDayChange = (newDay: number) => {
        if (selectedAgu === null) return;
        setAguList(prev => {
            if (!prev) return prev;
            const newAgus = prev.plannedAgus.map(agu =>
                agu.aguCui === selectedAgu ? { ...agu, dayOfThePlaning: newDay } : agu
            );
            return { ...prev, plannedAgus: newAgus };
        });
        setDayChangeDialogOpen(false);
    };

    const handleAddNewAgu = () => {
        if (selectedDay === null) return;
        setAguList(prev => {
            if (!prev) return prev;
            const newAguCui = prev.plannedAgus.length ? Math.max(...prev.plannedAgus.map(agu => agu.aguCui)) + 1 : 1;
            const newAgus = [...prev.plannedAgus, { ...newAgu, aguCui: newAguCui, dayOfThePlaning: selectedDay }];
            return { ...prev, plannedAgus: newAgus };
        });
        setNewAgu({ aguName: '', currentGasLevel: 0, plannedGasLevel: 0, dayOfThePlaning: 1 });
        setDialogOpen(false);
    };

    const handleConfirm = () => {
        const confirmedAgus = aguList?.plannedAgus.filter(agu => checkedPlans.has(agu.aguCui));
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

    const handleOpenDayChangeDialog = (aguCui: number) => {
        setSelectedAgu(aguCui);
        setDayChangeDialogOpen(true);
    };

    const handleCloseDayChangeDialog = () => {
        setDayChangeDialogOpen(false);
    };

    const daysOfWeek = ["Segunda", "Terça", "Quarta", "Quinta", "Sexta"];

    if (!aguList) return (
        <div>
            <WeeklyPlanHeader weekDate="Loading..." />
        </div>
    );

    return (
        <div>
            <WeeklyPlanHeader weekDate={`${aguList.weekStartDay} - ${aguList.weekEndDay}`} />
            {daysOfWeek.map((day, index) => {
                const dayPlans = aguList.plannedAgus.filter(plan => plan.dayOfThePlaning === index + 1) || [];
                const dayOfWeek = aguList.weekStartDay.split("/").map(Number)[0] + index;
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
                <DialogTitle>Add New Planned AGU</DialogTitle>
                <DialogContent>
                    <TextField
                        label="AGU Name"
                        value={newAgu.aguName}
                        onChange={(e) => setNewAgu({ ...newAgu, aguName: e.target.value })}
                        fullWidth
                        sx={{ marginBottom: 2 }}
                    />
                    <TextField
                        label="Current Gas Level"
                        type="number"
                        value={newAgu.currentGasLevel}
                        onChange={(e) => setNewAgu({ ...newAgu, currentGasLevel: parseInt(e.target.value) })}
                        fullWidth
                        sx={{ marginBottom: 2 }}
                    />
                    <TextField
                        label="Planned Gas Level"
                        type="number"
                        value={newAgu.plannedGasLevel}
                        onChange={(e) => setNewAgu({ ...newAgu, plannedGasLevel: parseInt(e.target.value) })}
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
                <DialogTitle>Change Day of Planned AGU</DialogTitle>
                <DialogContent>
                    <FormControl fullWidth sx={{ marginBottom: 2 }}>
                        <InputLabel>Day of the Planning</InputLabel>
                        <Select
                            value={newAgu.dayOfThePlaning.toString()}  // Convert number to string
                            onChange={(e) => setNewAgu({ ...newAgu, dayOfThePlaning: parseInt(e.target.value as string) })}
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
                    <Button onClick={() => handleDayChange(newAgu.dayOfThePlaning)} color="primary">
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