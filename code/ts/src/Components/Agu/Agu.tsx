import * as React from 'react'
import { useParams } from "react-router-dom";
import AguHeader from './AguHeader';
import { useEffect, useState } from "react";
import { Box, CircularProgress } from "@mui/material";
import AguBody from "./AguBody";

interface AguDetails {
    aguOrd: string
    aguName: string
    aguMetres: number
    aguCUI: string
    contacts: string[]
    isFavorite: boolean
    notes: string
    lvlMin: number
    lvlMax: number
    lvlMinHist: number
    latitude: number
    longitude: number
}

type AguState =
    | { type: 'loading' }
    | { type: 'error'; message: string }
    | { type: 'success'; aguDetails: AguDetails };

export default function Agu(){
    const [state, setState] = useState<AguState>({ type: 'loading' });
    const {aguId} = useParams<{ aguId: string }>();

    useEffect(() => {
        setState({ type: 'loading' });
        // TODO: fetch UAG details, for now we just simulate a successful fetch
        setTimeout(() => {
            if(aguId == null) return
            setState({
                type: 'success',
                aguDetails: {
                    aguOrd: aguId + " ORD",
                    aguName: aguId,
                    aguMetres: 100,
                    aguCUI: aguId + " CUI",
                    contacts: ['Pedro: 93XXXXXXX', 'Ricardo: 91XXXXXXX'],
                    isFavorite: aguId.length % 2 == 0,
                    notes: aguId + " NOTES\nNext line note",
                    lvlMin: 30,
                    lvlMax: 80,
                    lvlMinHist: 15,
                    latitude: 40.73061,
                    longitude: -73.935242,
                },
            });
        }, 1000);
    }, [aguId]);

    if(state.type == 'loading'){
        return (
            <Box>
                <CircularProgress />
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
            <AguHeader aguOrd={state.aguDetails.aguOrd} aguName={state.aguDetails.aguName} aguCUI={state.aguDetails.aguCUI} aguMetres={state.aguDetails.aguMetres} contacts={state.aguDetails.contacts} aguIsFavorite={state.aguDetails.isFavorite} />
            <br/>
            <AguBody aguNotes={state.aguDetails.notes} lvlMin={state.aguDetails.lvlMin} lvlMax={state.aguDetails.lvlMax} lvlMinHist={state.aguDetails.lvlMinHist} latitude={state.aguDetails.latitude} longitude={state.aguDetails.longitude} />
        </Box>
    )
}