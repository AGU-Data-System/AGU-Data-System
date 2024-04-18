import * as React from 'react'
import { Button } from "@mui/material";
import { useNavigate } from "react-router-dom";
import ArrowBack from '@mui/icons-material/ArrowBack';


export default function ReturnButton(){
    const navigate = useNavigate()
    return(
        <Button
            size="small"
            variant="contained"
            sx={{
                backgroundColor: 'rgb(255, 165, 0)',
                color: 'black',
                '&:hover': {
                    backgroundColor: 'rgba(255,165,0,0.49)',
                },
            }}
            onClick={() => navigate("/")}
        >
            <ArrowBack />
            Menu Inicial
        </Button>
    )
}