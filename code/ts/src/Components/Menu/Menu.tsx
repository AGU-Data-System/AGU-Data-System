import * as React from 'react';
import { useState } from 'react';
import { Grid } from '@mui/material';
import { ControlMenuLeft, ControlMenuRight } from '../ControlMenu/ControlMenu';
import LeafletMap from "../Map/LeafletMap";

export default function Menu({ darkMode }: { darkMode: boolean }) {
    const [filter, setFilter] = useState<string>('');

    return (
        <Grid container spacing={2} sx={{ marginTop: 2 }}>
            <Grid item xs={4}>
                <ControlMenuLeft />
            </Grid>
            <Grid item xs={4}>
                <LeafletMap filter={filter} darkMode={darkMode} />
            </Grid>
            <Grid item xs={4}>
                <ControlMenuRight setFilter={setFilter} />
            </Grid>
        </Grid>
    );
}