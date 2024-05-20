import * as React from 'react';
import {useEffect, useState} from 'react';
import {TextField, Typography, Grid, Card, CardContent, CardMedia, Box, CircularProgress} from '@mui/material';
import TemperatureOutputModel from "../../services/agu/models/temperatureOutputModel";
import { aguService } from "../../services/agu/aguService";
import { Problem } from "../../utils/Problem";
import LineGraph from "../Graphs/LineGraph";
import { TemperatureError } from "../Layouts/Error";

type TempGraphState =
    | { type: 'loading' }
    | { type: 'error'; message: string }
    | { type: 'success'; tempData: TemperatureOutputModel[] };

export default function AguBody(
    { aguCui, aguNotes, lvlMin, lvlMax, lvlCrit, latitude, longitude }: { aguCui: string, aguNotes: string; lvlMin: number; lvlMax: number; lvlCrit: number; latitude: number; longitude: number}
) {
    const [notes, setNotes] = useState(aguNotes);
    const [fetchingNotes, setFetchingNotes] = useState(false);
    const [tempState, setTempState] = useState<TempGraphState>({ type: 'loading' });

    const handleNotesChange = (event: any) => {
        setNotes(event.target.value);
    };

    const handleSubmitNotes = () => {
        setFetchingNotes(true);

        const updateNotes = async () => {
            const updatedNotes = await aguService.updateAguNotes(aguCui, notes);

            if (updatedNotes.value instanceof Error) {
                setTempState({ type: 'error', message: updatedNotes.value.message });
            } else if (updatedNotes.value instanceof Problem) {
                setTempState({ type: 'error', message: updatedNotes.value.title });
            } else {
                setFetchingNotes(false);
            }
        }

        updateNotes();
    };

    useEffect(() => {
        const fetch = async () => {
            setTempState({ type: 'loading' });

            const tempData = await aguService.getTemperatureData(aguCui);

            if (tempData.value instanceof Error) {
                setTempState({ type: 'error', message: tempData.value.message });
            } else if (tempData.value instanceof Problem) {
                setTempState({ type: 'error', message: tempData.value.title });
            } else {
                setTempState({type: 'success', tempData: tempData.value});
            }
        }

        fetch();

    }, [aguCui]);

    return (
        <Grid container spacing={3}>
            <Grid item xs={6} sx={{ display: 'flex', flexDirection: 'column' }}>
                <TextField
                    multiline
                    rows={4}
                    label="Notes"
                    value={notes}
                    onChange={handleNotesChange}
                    fullWidth
                    disabled={fetchingNotes}
                    onBlur={handleSubmitNotes}
                />
                <Box paddingTop='10%' paddingBottom='10%'>
                    <Typography variant="h6">Valor Máximo: {lvlMax}</Typography>
                    <Typography variant="h6">Valor Minimo: {lvlMin}</Typography>
                    <Typography variant="h6">Valor Crítico: {lvlCrit}</Typography>
                </Box>
                <Card sx={{ display:'flex', flexDirection:'row' }}>
                    <CardMedia
                        component="img"
                        image="https://via.placeholder.com/236x100"
                        alt="Placeholder"
                        sx={{ maxWidth: '50%' }}
                    />
                    <CardContent>
                        <Typography gutterBottom variant="h5" component="div">
                            Location
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                            Google Maps: <a target="_blank" href={`https://www.google.com/maps/place/${latitude},${longitude}`} rel="noreferrer">Link</a>
                        </Typography>
                    </CardContent>
                </Card>
            </Grid>

            <Grid item xs={6} sx={{ width: '100%' }}>
                <Card>
                    <CardContent>
                        {tempState.type === 'loading' && (
                            <Box sx={{ margin: 2, display: 'flex', justifyContent: 'center', alignItems: 'center', flexDirection: 'column'}}>
                                <Typography variant="h5" gutterBottom>Loading...</Typography>
                                <CircularProgress sx={{ color: 'rgb(255, 165, 0)' }}/>
                            </Box>
                        )}
                        {tempState.type === 'error' && (
                            <TemperatureError message={tempState.message} />
                        )}
                        {tempState.type === 'success' && tempState.tempData.length > 0 && (
                            <LineGraph data={tempState.tempData} />
                        )}
                        {tempState.type === 'success' && tempState.tempData.length === 0 && (
                            <Typography variant="h5" gutterBottom>Sem data de temperatura</Typography>
                        )}
                    </CardContent>
                </Card>
            </Grid>
        </Grid>
    );
}
