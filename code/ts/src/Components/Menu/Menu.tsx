import * as React from 'react';
import { Grid } from '@mui/material';
import { ControlMenuLeft, ControlMenuRight } from '../ControlMenu/ControlMenu';
import Map from '../Map/Map';

export default function Menu() {
    return (
        <Grid container spacing={2} sx={{ marginTop: 2 }}>
            <Grid item xs={4}>
                <ControlMenuLeft />
            </Grid>
            <Grid item xs={4}>
                <Map />
            </Grid>
            <Grid item xs={4}>
                <ControlMenuRight />
            </Grid>
        </Grid>
    );
}