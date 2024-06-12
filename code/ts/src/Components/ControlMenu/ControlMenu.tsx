import * as React from "react";
import {Button, Card, CardContent, Divider} from "@mui/material";
import Typography from "@mui/material/Typography";
import {useNavigate} from "react-router-dom";
import {useState} from "react";

export function ControlMenuLeft() {
    const navigate = useNavigate();
    return (
        <Card>
            <CardContent>
                <Typography variant="h5" component="div">
                    Menu de admistração
                </Typography>
                <br/>
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
                    }} onClick={() => {
                        navigate('/uag/create')
                    }}>
                        Adicionar UAG
                    </Button>
                </div>
                <br/>
                <Divider orientation="horizontal" flexItem/>
            </CardContent>
        </Card>
    );
}

export function ControlMenuRight({ setFilter } : { setFilter: (filter: string) => void }) {
    const [activeButton, setActiveButton] = useState<string>("");

    const handleClick = (filter: string) => {
        if (activeButton === filter) {
            setFilter("");
            setActiveButton("");
            return;
        }
        setFilter(filter);
        setActiveButton(filter);
    }

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
                    <Button
                        variant="contained"
                        sx={{
                            margin: '8px',
                            backgroundColor: activeButton === "norte" ? 'rgba(255,165,0,0.49)' : 'rgb(255, 165, 0)',
                            height: '100%',
                            color: 'black',
                            '&:hover': {
                                backgroundColor: 'rgba(255,165,0,0.49)',
                            },
                        }}
                        onClick={() => handleClick("norte")}
                    >
                        Norte
                    </Button>
                    <Button
                        variant="contained"
                        sx={{
                            margin: '8px',
                            backgroundColor: activeButton === "centro" ? 'rgba(255,165,0,0.49)' : 'rgb(255, 165, 0)',
                            height: '100%',
                            color: 'black',
                            '&:hover': {
                                backgroundColor: 'rgba(255,165,0,0.49)',
                            },
                        }}
                        onClick={() => handleClick("centro")}
                    >
                        Centro
                    </Button>
                    <Button
                        variant="contained"
                        sx={{
                            margin: '8px',
                            backgroundColor: activeButton === "sul" ? 'rgba(255,165,0,0.49)' : 'rgb(255, 165, 0)',
                            height: '100%',
                            color: 'black',
                            '&:hover': {
                                backgroundColor: 'rgba(255,165,0,0.49)',
                            },
                        }}
                        onClick={() => handleClick("sul")}
                    >
                        Sul
                    </Button>
                </div>
                <div
                    style={{
                        display: "flex",
                        flexDirection: "row",
                        flexWrap: "wrap",
                        justifyContent: "flex-start",
                    }}
                >
                    <Button
                        variant="contained"
                        sx={{
                            margin: '8px',
                            backgroundColor: activeButton === "sng" ? 'rgba(255,165,0,0.49)' : 'rgb(255, 165, 0)',
                            height: '100%',
                            color: 'black',
                            '&:hover': {
                                backgroundColor: 'rgba(255,165,0,0.49)',
                            },
                        }}
                        onClick={() => handleClick("sng")}
                    >
                        Sonorgás
                    </Button>
                    <Button
                        variant="contained"
                        sx={{
                            margin: '8px',
                            backgroundColor: activeButton === "tgg" ? 'rgba(255,165,0,0.49)' : 'rgb(255, 165, 0)',
                            height: '100%',
                            color: 'black',
                            '&:hover': {
                                backgroundColor: 'rgba(255,165,0,0.49)',
                            },
                        }}
                        onClick={() => handleClick("tgg")}
                    >
                        Tagusgás
                    </Button>
                    <Button
                        variant="contained"
                        sx={{
                            margin: '8px',
                            backgroundColor: activeButton === "dur" ? 'rgba(255,165,0,0.49)' : 'rgb(255, 165, 0)',
                            height: '100%',
                            color: 'black',
                            '&:hover': {
                                backgroundColor: 'rgba(255,165,0,0.49)',
                            },
                        }}
                        onClick={() => handleClick("dur")}
                    >
                        Duriensegás
                    </Button>
                </div>
                <br />
                <Divider orientation="horizontal" flexItem />
            </CardContent>
        </Card>
    );
}