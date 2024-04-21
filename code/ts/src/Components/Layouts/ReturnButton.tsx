import * as React from 'react'
import { Button } from "@mui/material";
import { useNavigate } from "react-router-dom";
import KeyboardArrowLeftIcon from '@mui/icons-material/KeyboardArrowLeft';


export default function ReturnButton(){
    const navigate = useNavigate()
    return(
        <Button
            size="small"
            variant="contained"
            sx={{
                backgroundColor: 'rgb(255, 165, 0)',
                height: '100%',
                color: 'black',
                '&:hover': {
                    backgroundColor: 'rgba(255,165,0,0.49)',
                },
            }}
            onClick={() => navigate("/")}
        >
            <KeyboardArrowLeftIcon />
            Menu Inicial
        </Button>
    )
}