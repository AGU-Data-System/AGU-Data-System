import * as React from 'react';
import { BarChart } from '@mui/x-charts/BarChart';

export default function BarGraph( data: number[], day: string[], period: string) {

    let plot = [];
    for (let i = 0; i < data.length; i++) {
        plot.push({ data: [data[i]], stack: day[i], label: period });
    }

    return (
        <BarChart
            series={plot}
            width={600} // TODO this should be responsive
            height={350} // TODO this should be responsive
        />
    );
}
