import * as React from 'react';
import { LineChart } from '@mui/x-charts/LineChart';
import { LineSeriesType } from '@mui/x-charts';

export default function LineGraph({ data, scale } : { data: number[][], scale :string }) {

    let buildScale: string[] = [];
    switch (scale) {
        case 'day':
            buildScale = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
            break;
        case 'month':
            buildScale = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
            break;
        case 'year':
            buildScale = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
            break;
    }

    const buildPlot: LineSeriesType[] = [];
    for (let i = 0; i < data.length; i++) {
        buildPlot.push({type: "line", curve: "linear", data: data[i]});
    }

    return (
        <LineChart
            xAxis={[{ data: buildScale }]}
            series={buildPlot}
            width={500}
            height={300}
        />
    );
}