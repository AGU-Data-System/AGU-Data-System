import * as React from 'react';
import { BarChart } from '@mui/x-charts/BarChart';
import { axisClasses } from '@mui/x-charts/ChartsAxis';
import { GasOutputModel } from "../../services/agu/models/gasOutputModel";
import { Box } from "@mui/material";
import { useState } from "react";
import { aguService } from "../../services/agu/aguService";
import { Problem } from "../../utils/Problem";

const valueFormatter = (value: number | null) => `${value}%`;

const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const hour = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const month = date.toLocaleString('default', { month: 'short' }).toUpperCase();
    const year = date.getFullYear();
    return { hour, minutes, day, month, year };
};

export default function BarsDataset({ gasData, gasPredData, aguCui, aguMin, aguMax, aguCrit }: { gasData: GasOutputModel[], gasPredData: GasOutputModel[], aguCui: string, aguMin: number, aguMax: number, aguCrit: number }) {
    const [dayData, setDayData] = useState<GasOutputModel[]>([]);

    const handleAxisClick = (event: any, d: any) => {
        const fetchHourlyGasData = async (day: number, month: number, year: number) => {
            const hourlyGasData = await aguService.getHourlyGasData(aguCui, day, month, year);

            if (hourlyGasData.value instanceof Error) {
                setDayData([]);
            } else if (hourlyGasData.value instanceof Problem) {
                setDayData([]);
            } else {
                setDayData(hourlyGasData.value.gasMeasures);
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

    const formattedData = [...gasData, ...gasPredData].map((item) => {
        const { day, month, year } = formatDate(item.predictionFor);
        return {
            ...item,
            day,
            month,
            year,
            actualLevel: gasData.includes(item) ? item.level : null,
            predictedLevel: gasPredData.includes(item) ? item.level : null,
        };
    }).sort((a, b) => a.predictionFor.localeCompare(b.predictionFor));

    // Combine data with the same day, month, and year
    const combinedDataMap = new Map();

    formattedData.forEach(item => {
        const key = `${item.day}-${item.month}-${item.year}`;
        if (!combinedDataMap.has(key)) {
            combinedDataMap.set(key, { ...item });
        } else {
            const existingItem = combinedDataMap.get(key);
            combinedDataMap.set(key, {
                ...existingItem,
                actualLevel: item.actualLevel !== null ? item.actualLevel : existingItem.actualLevel,
                predictedLevel: item.predictedLevel !== null ? item.predictedLevel : existingItem.predictedLevel,
            });
        }
    });

    const combinedData = Array.from(combinedDataMap.values());

    const formattedDayData = dayData.map((item) => {
        const { hour, minutes, day, month, year } = formatDate(item.predictionFor);
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

    const chartSetting = {
        yAxis: [
            {
                label: 'Nivel de gás (%)',
                max: 100,
            },
        ],
        sx: {
            [`.${axisClasses.left} .${axisClasses.label}`]: {
                transform: 'translate(-10px, 0)',
            },
        },
    };

    const minY = 250 - aguMin * 2;
    const maxY = 250 - aguMax * 2;
    const critY = 250 - aguCrit * 2;

    const critYOffset = aguMin === aguCrit ? -10 : 0;

    return (
        <Box>
            <div style={{ width: '100%', height: '100%' }}>
                <div style={{ textAlign: 'center', marginBottom: '10px' }}>
                    {combinedData.length > 0 && (
                        <div>
                            <span>{combinedData[0].month} {combinedData[0].year}</span>
                        </div>
                    )}
                </div>
                <div style={{ width: '100%', height: 'calc(100% - 40px)' }}>
                    <BarChart
                        dataset={combinedData}
                        xAxis={[{scaleType: 'band', dataKey: 'day', label: 'Dia'}]}
                        series={[
                            {dataKey: 'actualLevel', label: 'Gás atual', valueFormatter, color: 'orange'},
                            {dataKey: 'predictedLevel', label: 'Gás previsto', valueFormatter, color: 'grey'},
                        ]}
                        onAxisClick={(event, d) => handleAxisClick(event, d)}
                        width={500}
                        height={300}
                        {...chartSetting}
                    >
                        <line x1="50" x2="90%" y1={maxY} y2={maxY} stroke="green" strokeWidth="2"/>
                        <line x1="50" x2="90%" y1={minY} y2={minY} stroke="blue" strokeWidth="2"/>
                        <line x1="50" x2="90%" y1={critY} y2={critY} stroke="red" strokeWidth="2"/>

                        <text x="91%" y={maxY} fill="green">Max</text>
                        <text x="91%" y={minY} fill="blue">Min{critYOffset != 0 ? ' &' : ''}</text>
                        <text x="91%" y={critY + critYOffset} fill="red">Crít</text>
                    </BarChart>
                </div>
            </div>
            <div style={{width: '100%', height: '100%'}}>
                <div style={{textAlign: 'center', marginBottom: '10px'}}>
                    {formattedDayData.length > 0 && (
                        <div>
                            <span>{formattedDayData[0].day} {formattedDayData[0].month} {formattedDayData[0].year}</span>
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
                    >
                        <line x1="50" x2="90%" y1={maxY} y2={maxY} stroke="green" strokeWidth="2"/>
                        <line x1="50" x2="90%" y1={minY} y2={minY} stroke="blue" strokeWidth="2"/>
                        <line x1="50" x2="90%" y1={critY} y2={critY} stroke="red" strokeWidth="2"/>

                        <text x="91%" y={maxY} fill="green">Max</text>
                        <text x="91%" y={minY} fill="blue">Min{critYOffset!=0 ? ' &' : ''}</text>
                        <text x="91%" y={critY - critYOffset} fill="red">Crítico</text>
                    </BarChart>
                </div>
            </div>
        </Box>
    );
}