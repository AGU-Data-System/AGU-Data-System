import * as React from 'react'
import ErrorOutlineOutlinedIcon from "@mui/icons-material/ErrorOutlineOutlined";
import Typography from "@mui/material/Typography";
import { ReloadButton } from "./Buttons";
import { Box } from "@mui/material";

export default function Error( {message}:{message: string} ) {
    return (
        <Box sx={{
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
            alignItems: 'center',
            height: '100vh',
        }}>
            <ErrorOutlineOutlinedIcon sx={{ fontSize: 100, color: 'red' }} />
            <Typography variant="h5" gutterBottom>Error while trying to display the map</Typography>
            <Typography variant="body1" gutterBottom>Reason: {message}</Typography>
            <ReloadButton />
        </Box>
    )
}