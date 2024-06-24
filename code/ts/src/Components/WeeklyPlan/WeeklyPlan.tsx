import * as React from 'react';
import { useEffect, useState } from 'react';
import { Box, Paper, Typography, IconButton, Collapse } from '@mui/material';
import { ExpandMore, ExpandLess } from '@mui/icons-material';
import { ReturnButton } from "../Layouts/Buttons";
import { WeeklyPlanOutputModel } from "../../services/agu/models/weeklyPlanOutputModel";

function DayCard({ day, date, plans } : { day: string, date: string, plans: Array<{aguName: string, currentGasLevel: number, plannedGasLevel: number}> }) {
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
                </Box>
            </Box>
            {plans.length > 0 && (
                <Collapse in={open}>
                    <Box sx={{ marginTop: 2, padding: 2, border: '1px solid black' }}>
                        {plans.map((plan, index) => (
                            <Box key={index} sx={{ marginBottom: 2 }}>
                                <Typography variant="h6">{plan.aguName}</Typography>
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
    const [aguList, setAguList] = useState<WeeklyPlanOutputModel>();

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
                }
            );
        }
        fetchWeeklyPlan();
    }, []);

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
                    />
                );
            })}
        </div>
    );
}