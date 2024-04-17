import * as React from 'react';
import { useEffect, useState } from "react";
import { CircularProgress, Grid, List, ListItem, ListItemText } from "@mui/material";
import StarRateIcon from '@mui/icons-material/StarRate';
import { useNavigate } from "react-router-dom";

interface UAGDetails {
    name: string
    currLvl: number
    nextLvl: number
    nextLvlDate: string
}

type FavBarState =
    | { type: 'loading' }
    | { type: 'error'; message: string }
    | { type: 'success'; uagsDetails: UAGDetails[] };

export default function FavBar() {
    const [state, setState] = useState<FavBarState>({ type: 'loading' });
    const navigate = useNavigate()

    useEffect(() => {
        setState({ type: 'loading' });
        // TODO: fetch UAGs details, for now we just simulate a successful fetch
        setTimeout(() => {
            setState({
                type: 'success',
                uagsDetails: [
                    { name: 'UAG1', currLvl: 30, nextLvl: 40, nextLvlDate: '16042024' },
                    { name: 'UAG2', currLvl: 50, nextLvl: 50, nextLvlDate: '17042024' },
                    { name: 'UAG3', currLvl: 80, nextLvl: 75, nextLvlDate: '18042024' },
                    { name: 'UAG5', currLvl: 50, nextLvl: 50, nextLvlDate: '17042024' },
                    { name: 'UAG6', currLvl: 80, nextLvl: 75, nextLvlDate: '18042024' },
                    { name: 'UAG7', currLvl: 30, nextLvl: 40, nextLvlDate: '16042024' },
                    { name: 'UAG8', currLvl: 50, nextLvl: 50, nextLvlDate: '17042024' },
                    { name: 'UAG9', currLvl: 80, nextLvl: 75, nextLvlDate: '18042024' },
                    { name: 'UAG10', currLvl: 30, nextLvl: 40, nextLvlDate: '16042024' },
                    { name: 'UAG11', currLvl: 50, nextLvl: 50, nextLvlDate: '17042024' },
                    { name: 'UAG12', currLvl: 80, nextLvl: 75, nextLvlDate: '18042024' },
                ],
            });
        }, 1000);
    }, []);

    if (state.type === 'loading') {
        return (
            <Grid container alignItems="center" sx={{ marginTop: '20px', border: 1, borderColor: 'rgb(255, 165, 0)', borderRadius: '16px', borderWidth: '6px'}}>
                <Grid item sx={{paddingTop: 2, paddingLeft: 2 }}>
                    <StarRateIcon /> Favorite UAGs
                </Grid>
                <Grid item sx={{ margin: 2 }}>
                    <CircularProgress />
                </Grid>
            </Grid>
            );
    }

    if (state.type === 'error') {
        return (
            <Grid container alignItems="center" sx={{ marginTop: '20px', border: 1, borderColor: 'rgb(255, 165, 0)', borderRadius: '16px', borderWidth: '6px'}}>
                <Grid item sx={{paddingTop: 2, paddingLeft: 2 }}>
                    <StarRateIcon /> Error while trying to get the favorite UAGs: {state.message}
                </Grid>
            </Grid>
        );
    }

    return (
        <Grid container alignItems="right" sx={{ marginTop: '20px', border: 1, borderColor: 'rgb(255, 165, 0)', borderRadius: '16px', borderWidth: '6px'}}>
            <Grid item sx={{paddingTop: 2, paddingLeft: 2, width: '100%'}}>
                <StarRateIcon /> Favorite UAGs
            </Grid>
            <Grid item sx={{ overflowX: 'auto', whiteSpace: 'nowrap' }}>
                <List sx={{ display: 'flex', flexDirection: 'row' }}>
                    {state.uagsDetails.map((uag, index) => (
                        <ListItem
                            key={index}
                            sx={{
                                display: 'flex',
                                flexDirection: 'column',
                                border: 1,
                                borderColor: 'rgb(255, 165, 0)',
                                borderRadius: '16px',
                                borderWidth: '6px',
                                margin: 2,
                                '&:hover': {
                                    backgroundColor: 'rgba(255,165,0,0.49)'
                                }
                            }}
                            onClick={() => navigate(`/uag/${uag.name}`)}
                        >
                            <ListItemText primary={`Name: ${uag.name}`} />
                            <ListItemText primary={`Curr Level: ${uag.currLvl}`} />
                            <ListItemText primary={`Next Level: ${uag.nextLvl}`} />
                            <ListItemText primary={`Next Level Date: ${uag.nextLvlDate}`} />
                        </ListItem>
                    ))}
                </List>
            </Grid>
        </Grid>
    );
}