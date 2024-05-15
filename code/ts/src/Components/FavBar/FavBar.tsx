import * as React from 'react';
import { useEffect, useState } from "react";
import { CircularProgress, Grid, List, ListItem, ListItemText } from "@mui/material";
import { useNavigate } from "react-router-dom";
import StarIcon from '@mui/icons-material/Star';
import { FavError } from "../Layouts/Error";
import Typography from "@mui/material/Typography";
import { aguService } from "../../services/agu/aguService";
import { Problem } from "../../utils/Problem";
import { AgusBasicInfoOutputModel } from "../../services/agu/models/aguOutputModel";
import WarningAmberOutlinedIcon from '@mui/icons-material/WarningAmberOutlined';

type FavBarState =
    | { type: 'loading' }
    | { type: 'error'; message: string | undefined }
    | { type: 'success'; uagsDetails: AgusBasicInfoOutputModel[] };

export default function FavBar() {
    const [state, setState] = useState<FavBarState>({ type: 'loading' });
    const navigate = useNavigate()

    useEffect(() => {
        setState({ type: 'loading' });
        const getFavAgus = async () => {
            const favAugs = await aguService.getFavouriteAgus();
            if (favAugs.value instanceof Error) {
                setState({ type: 'error', message: favAugs.value.message });
            } else if (favAugs.value instanceof Problem) {
                setState({ type: 'error', message: favAugs.value.detail });
            } else {
                setState({type: 'success', uagsDetails: favAugs.value});
            }
        }
        getFavAgus();
    }, []);

    if (state.type === 'loading') {
        return (
            <Grid container sx={{ marginTop: '20px', border: 1, borderColor: 'rgb(255, 165, 0)', borderRadius: '16px', borderWidth: '6px', display: 'flex', flexDirection: 'column'}}>
                <Grid item sx={{ paddingTop: 2, paddingLeft: 2 }}>
                    <StarIcon fontSize='large' sx={{ color: 'rgb(255, 165, 0)' }} /> Favorite UAGs
                </Grid>
                <Grid item sx={{ margin: 2, display: 'flex', justifyContent: 'center', alignItems: 'center', flexDirection: 'column'}}>
                    <Typography variant="h5" gutterBottom>Loading...</Typography>
                    <CircularProgress sx={{ color: 'rgb(255, 165, 0)' }}/>
                </Grid>
            </Grid>
        );
    }

    if (state.type === 'error') {
        return (
            <Grid container alignItems="center" sx={{ marginTop: '20px', border: 1, borderColor: 'rgb(255, 165, 0)', borderRadius: '16px', borderWidth: '6px'}}>
                <Grid item sx={{paddingTop: 2, paddingLeft: 2, paddingBottom: 2, width: '100%' }}>
                    <StarIcon fontSize='large' sx={{ color: 'rgb(255, 165, 0)' }} /> Favorite UAGs
                    <FavError message={state.message ? state.message : "Error fetching!"}/>
                </Grid>
            </Grid>
        );
    }

    return (
        <Grid container alignItems="right" sx={{ marginTop: '20px', border: 1, borderColor: 'rgb(255, 165, 0)', borderRadius: '16px', borderWidth: '6px'}}>
            <Grid item sx={{paddingTop: 2, paddingLeft: 2, width: '100%'}}>
                <StarIcon fontSize='large' sx={{ color: 'rgb(255, 165, 0)' }} /> UAGs Favoritas
            </Grid>
            <Grid item sx={{ overflowX: 'auto', whiteSpace: 'nowrap' }}>
                <List sx={{ display: 'flex', flexDirection: 'row' }}>
                    {
                        state.uagsDetails.length === 0 &&
                        <ListItem sx={{ display: 'flex', flexDirection: 'row', margin: 2 }}>
                            <WarningAmberOutlinedIcon sx={{ color: 'rgb(255, 165, 0)', marginRight: 1 }}/> <ListItemText primary="Sem UAGs favoritas!" />
                        </ListItem>
                    }
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
                            onClick={() => navigate(`/uag/${uag.cui}`)}
                        >
                            <ListItemText primary={`Nome: ${uag.name}`} />
                            <ListItemText primary={`ORD: ${uag.dno.name}`} />
                            {/*<ListItemText primary={`Curr Level: ${uag.currLvl}`} />
                            <ListItemText primary={`Next Level: ${uag.nextLvl}`} />
                            <ListItemText primary={`Next Level Date: ${uag.nextLvlDate}`} />*/}
                        </ListItem>
                    ))}
                </List>
            </Grid>
        </Grid>
    );
}