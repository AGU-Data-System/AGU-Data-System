import * as React from 'react';
import { BarChart } from '@mui/x-charts/BarChart';
import { axisClasses } from '@mui/x-charts/ChartsAxis';
import GasOutputModel from "../../services/agu/models/gasOutputModel";
import { Box } from "@mui/material";
import { useState } from "react";
import { aguService } from "../../services/agu/aguService";
import { Problem } from "../../utils/Problem";

const chartSetting = {
    yAxis: [
        {
            label: 'Nivel de gás',
        },
    ],
    sx: {
        [`.${axisClasses.left} .${axisClasses.label}`]: {
            transform: 'translate(-10px, 0)',
        },
    },
};

const valueFormatter = (value: number | null) => `${value}`;

const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const hour = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const month = date.toLocaleString('default', { month: 'short' }).toUpperCase();
    const year = date.getFullYear();
    return { hour, minutes, day, month, year };
};

export default function BarsDataset({ data, aguCui }: { data: GasOutputModel[], aguCui: string }) {
    const [dayData, setDayData] = useState<GasOutputModel[]>([]);

    const handleAxisClick = (event: any, d: any) => {
        const fetchHourlyGasData = async (day: number, month: number, year: number) => {
            const hourlyGasData = await aguService.getHourlyGasData(aguCui, day, month, year);

            if (hourlyGasData.value instanceof Error) {
                setDayData([]);
            } else if (hourlyGasData.value instanceof Problem) {
                setDayData([]);
            } else {
                setDayData(hourlyGasData.value);
            }
        };

        const { day, month, year } = dToDate(d);
        fetchHourlyGasData(day, month, year);
    };

    const dToDate = (d: any) => {
        const axisValue = d.axisValue;
        const dataEntry = formattedData.find(item => item.day === axisValue)?.predictionFor;
        if (!dataEntry) return { day: 0, month: 0, year: 0 };
        const date = new Date(dataEntry);
        return { day: date.getDate(), month: date.getMonth() + 1, year: date.getFullYear() };
    };

    const formattedData = data.map((item) => {
        const { day, month, year } = formatDate(item.predictionFor);
        return {
            ...item,
            day,
            month,
            year,
        };
    }).sort((a, b) => a.predictionFor.localeCompare(b.predictionFor));

    const formattedDayData = dayData.map((item) => {
        const { hour, minutes, day, month, year } = formatDate(item.timestamp);
        return {
            ...item,
            hour,
            minutes,
            day,
            month,
            year,
            hourMinutes: `${hour}:${minutes}`,
        };
    }).sort((a, b) => a.timestamp.localeCompare(b.timestamp)); // Sort by timestamp for accuracy

    return (
        <Box>
            <div style={{ width: '100%', height: '100%' }}>
                <div style={{ textAlign: 'center', marginBottom: '10px' }}>
                    {formattedData.length > 0 && (
                        <div>
                            <span>{formattedData[0].month} {formattedData[0].year}</span>
                        </div>
                    )}
                </div>
                <div style={{ width: '100%', height: 'calc(100% - 40px)' }}>
                    <BarChart
                        dataset={formattedData}
                        xAxis={[{ scaleType: 'band', dataKey: 'day', label: 'Dia' }]}
                        series={[
                            { dataKey: 'level', label: 'Level', valueFormatter, color: 'orange' },
                        ]}
                        onAxisClick={(event, d) => handleAxisClick(event, d)}
                        width={500}
                        height={300}
                        {...chartSetting}
                    />
                </div>
            </div>
            <div style={{ width: '100%', height: '100%' }}>
                <div style={{ textAlign: 'center', marginBottom: '10px' }}>
                    {formattedDayData.length > 0 && (
                        <div>
                            <span>{formattedDayData[0].month} {formattedDayData[0].year}</span>
                        </div>
                    )}
                </div>
                <div style={{ width: '100%', height: 'calc(100% - 40px)' }}>
                    <BarChart
                        dataset={formattedDayData}
                        xAxis={[{ scaleType: 'band', dataKey: 'hourMinutes', label: 'Horas:Minutos' }]}
                        series={[
                            { dataKey: 'level', label: 'Level', valueFormatter, color: 'orange' },
                        ]}
                        width={500}
                        height={300}
                        {...chartSetting}
                    />
                </div>
            </div>
        </Box>
    );
}