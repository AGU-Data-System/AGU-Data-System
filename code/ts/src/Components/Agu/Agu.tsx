import * as React from 'react'
import { useParams } from "react-router-dom";
import AguHeader from './AguHeader';
import { useEffect, useState } from "react";
import { Box, CircularProgress } from "@mui/material";

interface AguDetails {
    aguOrd: string
    aguName: string
    contacts: string[]
    isFavorite: boolean
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
                    contacts: ['991293', '232342342342'],
                    isFavorite: aguId.length % 2 == 0,
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
            <AguHeader aguOrd={state.aguDetails.aguOrd} aguName={state.aguDetails.aguName} contacts={state.aguDetails.contacts} isFavorite={state.aguDetails.isFavorite} />
        </Box>
    )
}