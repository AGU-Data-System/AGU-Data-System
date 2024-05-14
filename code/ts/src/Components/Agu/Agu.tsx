import * as React from 'react'
import { useParams } from "react-router-dom";
import AguHeader from './AguHeader';
import { useEffect, useState } from "react";
import { Box, CircularProgress } from "@mui/material";
import AguBody from "./AguBody";
import { AguDetailsOutputModel } from "../../services/agu/models/aguOutputModel";
import {aguService} from "../../services/agu/aguService";

type AguState =
    | { type: 'loading' }
    | { type: 'error'; message: string }
    | { type: 'success'; aguDetails: AguDetailsOutputModel };

export default function Agu(){
    const [state, setState] = useState<AguState>({ type: 'loading' });
    const {aguId} = useParams<{ aguId: string }>();

    useEffect(() => {
        const fetch = async () => {
            setState({ type: 'loading' });
            if (!aguId) {
                setState({ type: 'error', message: 'No AGU ID provided' });
                return;
            }

            const getAguDetails = await aguService.getAguById(aguId);

            if (getAguDetails.value instanceof Error) {
                setState({ type: 'error', message: getAguDetails.value.message });
            } else {
                setState({type: 'success', aguDetails: getAguDetails.value});
            }
        }
        fetch();
    }, [aguId]);

    if(state.type == 'loading'){
        return (
            <Box>
                <CircularProgress sx={{ color: 'rgb(255, 165, 0)' }}/>
            </Box>
        )
    }

    if (state.type == 'error') {
        return(
            <Box>
                Error: {state.message}
            </Box>
        )
    }

    return(
        <Box>
            <AguHeader aguOrd={state.aguDetails.dno.name} aguName={state.aguDetails.name} aguCUI={state.aguDetails.cui} aguMetres={state.aguDetails.capacity} contacts={state.aguDetails.contacts} aguIsFavorite={state.aguDetails.isFavorite} />
            <br/>
            <AguBody aguNotes={state.aguDetails.notes ? state.aguDetails.notes : ""} lvlMin={state.aguDetails.levels.min} lvlMax={state.aguDetails.levels.max} lvlCrit={state.aguDetails.levels.critical} latitude={state.aguDetails.location.latitude} longitude={state.aguDetails.location.longitude} />
        </Box>
    )
}