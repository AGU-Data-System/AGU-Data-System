import * as React from 'react';
import { useState } from 'react';
import {TextField, Typography, Grid, Card, CardContent, CardMedia, Button, Box} from '@mui/material';

export default function AguBody(
    { aguNotes, lvlMin, lvlMax, lvlMinHist, latitude, longitude }: { aguNotes: string; lvlMin: number; lvlMax: number; lvlMinHist: number; latitude: number; longitude: number}
) {
    const [notes, setNotes] = useState(aguNotes);
    const [isEditing, setIsEditing] = useState(false);

    const handleNotesChange = (event: any) => {
        setNotes(event.target.value);
        console.log('Notes: ' + notes);
    };

    const handleSubmitNotes = () => {
        console.log('Notes submitted successfully: ' + notes);
    };

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
                    onFocus={() => setIsEditing(true)}
                    onBlur={() => setIsEditing(false)}
                />
                {isEditing && (
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleSubmitNotes}
                    >
                        Save Notes
                    </Button>
                )}
                <Box paddingTop='10%' paddingBottom='10%'>
                    <Typography variant="h6">Valor Máximo: {lvlMax}</Typography>
                    <Typography variant="h6">Valor Minimo: {lvlMin}</Typography>
                    <Typography variant="h6">Valor Minimo Histórico: {lvlMinHist}</Typography>
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

            <Grid item xs={6}>
                <Card>
                    <CardContent>
                        <Typography variant="h5" component="div">
                            Graphs/Charts
                        </Typography>
                    </CardContent>
                </Card>
            </Grid>
        </Grid>
    );
}
