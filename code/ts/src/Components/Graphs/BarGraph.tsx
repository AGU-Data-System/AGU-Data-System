import * as React from 'react';
import { useRef, useEffect, useState } from 'react';
import { BarChart } from '@mui/x-charts/BarChart';
import { axisClasses } from '@mui/x-charts/ChartsAxis';
import TemperatureOutputModel from "../../services/agu/models/temperatureOutputModel";

const chartSetting = {
    yAxis: [
        {
            label: 'Temperatura (°C)',
        },
    ],
    sx: {
        [`.${axisClasses.left} .${axisClasses.label}`]: {
            transform: 'translate(-20px, 0)',
        },
    },
};

const valueFormatter = (value: number | null) => `${value}°C`;

const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const day = date.getDate().toString().padStart(2, '0');
    const month = date.toLocaleString('default', { month: 'short' }); // e.g., "May"
    const year = date.getFullYear();
    return { day, month, year };
};

export default function BarsDataset({ data }: { data: TemperatureOutputModel[] }) {
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
        handleResize(); // Initial size calculation

        return () => {
            window.removeEventListener('resize', handleResize);
        };
    }, []);

    const formattedData = data.map((item) => {
        const { day, month, year } = formatDate(item.predictionFor);
        return {
            ...item,
            day, // Add day for x-axis
            month, // Add month for display
            year, // Add year for display
        };
    }).reverse(); // Reverse the data to display from left to right

    return (
        <div ref={containerRef} style={{ width: '100%', height: '100%' }}>
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
                    xAxis={[{ scaleType: 'band', dataKey: 'day', label: 'Day' }]}
                    series={[
                        { dataKey: 'min', label: 'Min', valueFormatter, color: 'blue' },
                        { dataKey: 'max', label: 'Max', valueFormatter, color: 'red' },
                    ]}
                    width={dimensions.width}
                    height={dimensions.height}
                    {...chartSetting}
                />
            </div>
        </div>
    );
}
