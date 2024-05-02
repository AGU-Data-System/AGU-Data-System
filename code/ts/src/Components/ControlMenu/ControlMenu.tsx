import * as React from "react";
import {Button, Card, CardContent, Divider} from "@mui/material";
import Typography from "@mui/material/Typography";
import {useNavigate} from "react-router-dom";

export function ControlMenuLeft() {
    return (
        <Card>
            <CardContent>
                <Typography variant="h5" component="div">
                    Menu de Controlo
                </Typography>
                <br/>
                <Divider orientation="horizontal" flexItem/>
            </CardContent>
        </Card>
    );
}

export function ControlMenuRight() {
    const navigate = useNavigate();

    return (
        <Card>
            <CardContent>
                <Typography variant="h5" component="div">
                    Menu de Acessibilidade
                </Typography>
                <br />
                <div
                    style={{
                        display: "flex",
                        flexDirection: "row",
                        flexWrap: "wrap",
                        justifyContent: "flex-start",
                    }}
                >
                    <Button variant="contained" sx={{
                        margin: '8px',
                        backgroundColor: 'rgb(255, 165, 0)',
                        height: '100%',
                        color: 'black',
                        '&:hover': {
                            backgroundColor: 'rgba(255,165,0,0.49)',
                        },
                    }} onClick={() => {navigate('/uag/create')}}>
                        Adicionar UAG
                    </Button>
                </div>
                <br />
                <Divider orientation="horizontal" flexItem />
            </CardContent>
        </Card>
    );
}