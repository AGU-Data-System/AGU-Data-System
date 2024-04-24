import * as React from 'react';
import { PieChart } from '@mui/x-charts/PieChart';

interface CompanyData {
    value: number;
    label: string;
}

export default function PieGraph({ data }: { data: CompanyData[] }) {

    const plot = [];
    for (let i = 0; i < data.length; i++) {
        plot.push({ value: data[i].value, label: data[i].label });
    }

    return (
        <PieChart
            series={[
                {
                    data: plot,
                },
            ]}
            width={400}
            height={200}
        />
    );
}