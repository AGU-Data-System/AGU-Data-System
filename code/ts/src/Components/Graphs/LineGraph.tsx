import * as React from 'react';
import { useRef, useEffect, useState } from 'react';
import { LineChart } from '@mui/x-charts/LineChart';
import TemperatureOutputModel from "../../services/agu/models/temperatureOutputModel";
import { axisClasses } from "@mui/x-charts/ChartsAxis";

const chartSetting = {
    yAxis: [
        {
            label: 'Temperatura (°C)',
        },
    ],
    sx: {
        [`.${axisClasses.left} .${axisClasses.label}`]: {
            transform: 'translate(-10px, 0)',
        },
    },
};

const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const day = date.getDate().toString().padStart(2, '0');
    const month = date.toLocaleString('default', { month: 'short' });
    const year = date.getFullYear();
    return { day, month, year };
};

const valueFormatter = (value: number | null) => `${value}°C`;

const getMonthYearLabel = (data: TemperatureOutputModel[]) => {
    const months = new Set(data.map(item => {
        const date = new Date(item.predictionFor);
        return date.toLocaleString('default', { month: 'short' });
    }));
    const year = new Date(data[0].predictionFor).getFullYear();

    return `${Array.from(months).join('-')} ${year}`;
};

export default function LineGraph({ data }: { data: TemperatureOutputModel[] }) {
    const containerRef = useRef<HTMLDivElement>(null);
    const [dimensions, setDimensions] = useState({ width: 500, height: 300 });

    useEffect(() => {
        const handleResize = () => {
            if (containerRef.current) {
                setDimensions({
                    width: containerRef.current.clientWidth,
                    height: 300,
                });
            }
        };

        window.addEventListener('resize', handleResize);
        handleResize();

        return () => {
            window.removeEventListener('resize', handleResize);
        };
    }, []);

    const formattedData = data.map((item) => {
        const { day, month, year } = formatDate(item.predictionFor);
        return {
            ...item,
            day,
            month,
            year,
        };
    });

    const lineChartData = formattedData.map(item => ({
        day: item.day,
        max: item.max,
        min: item.min,
    }));

    const monthYearLabel = getMonthYearLabel(data);

    return (
        <div ref={containerRef} style={{ width: '100%', height: '100%', textAlign: 'center', marginBottom: '10px' }}>
            {formattedData.length > 0 && (
                <div>
                    <span>{monthYearLabel}</span>
                </div>
            )}
            <LineChart
                dataset={lineChartData}
                xAxis={[{ dataKey: 'day', scaleType: 'band', tickPlacement: 'middle', label: 'Dia' }]}
                series={[
                    { dataKey: 'max', label: 'Max', valueFormatter, curve: 'catmullRom', color: 'red' },
                    { dataKey: 'min', label: 'Min', valueFormatter, curve: 'catmullRom', color: 'orange' },
                ]}
                width={dimensions.width}
                height={dimensions.height}
                {...chartSetting}
            />
        </div>
    );
}