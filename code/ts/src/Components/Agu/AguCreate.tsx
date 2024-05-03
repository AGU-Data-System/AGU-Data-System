import * as React from 'react';
import { useState } from 'react';
import { Button, Container, Grid, TextField, Typography } from '@mui/material';
import AddBoxOutlinedIcon from '@mui/icons-material/AddBoxOutlined';
import IndeterminateCheckBoxOutlinedIcon from '@mui/icons-material/IndeterminateCheckBoxOutlined';
import ReturnButton from "../Layouts/ReturnButton";
import KeyboardArrowRightIcon from "@mui/icons-material/KeyboardArrowRight";

interface AGU {
    cui: string;
    name: string;
    minLevel: string;
    maxLevel: string;
    criticalLevel: string;
    loadVolume: string;
    latitude: string;
    longitude: string;
    locationName: string;
    ord: string;
}

interface Tank {
    number: number;
    minLevel: string;
    maxLevel: string;
    criticalLevel: string;
    loadVolume: string;
    capacity: string;
    [key: string]: string | number; // Index signature
}

interface Contact {
    name: string;
    phone: string;
    type: string;
}

export default function AguCreate() {
    const [aguData, setAGUData] = useState<AGU>({
        cui: '',
        name: '',
        minLevel: '',
        maxLevel: '',
        criticalLevel: '',
        loadVolume: '',
        latitude: '',
        longitude: '',
        locationName: '',
        ord: '',
    });
    const [tankData, setTankData] = useState<Tank[]>([]);
    const [contacts, setContacts] = useState<Contact[]>([]);

    const handleAGUChange = (prop: keyof typeof aguData) => (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        setAGUData({ ...aguData, [prop]: event.target.value });
    };

    const handleTankChange = (prop: keyof Tank, index: number) => (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const newTanks = [...tankData];
        newTanks[index][prop] = event.target.value;
        setTankData(newTanks);
    };

    const handleContactChange = (prop: keyof Contact, index: number) => (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const newContacts = [...contacts];
        newContacts[index][prop] = event.target.value;
        setContacts(newContacts);
    };

    const addTank = () => {
        setTankData([...tankData, {
            number: tankData.length + 1,
            minLevel: '',
            maxLevel: '',
            criticalLevel: '',
            loadVolume: '',
            capacity: '',
        }]);
    };

    const addContact = () => {
        setContacts([...contacts, {
            name: '',
            phone: '',
            type: 'emergency',
        }]);
    };

    const removeTank = (index: number) => {
        const newTanks = [...tankData];
        newTanks.splice(index, 1);

        for (let i = 0; i < newTanks.length; i++) {
            newTanks[i].number = i + 1;
        }

        setTankData(newTanks);
    };

    const removeContact = (index: number) => {
        const newContacts = [...contacts];
        newContacts.splice(index, 1);
        setContacts(newContacts);
    };

    const handleSubmit = () => {
        // Handle form submission (send data to backend)
        console.log({ aguData, tankData, contacts });
    };

    return (
        <Container style={{ marginTop: '40px' }}>
            <div style={{display: 'flex', justifyContent: 'flex-end'}}>
                <ReturnButton/>
            </div>
            <Typography variant="h4" align="center" gutterBottom>
                AGU Details
            </Typography>
            <Grid container spacing={3}>
                <Grid item xs={6}>
                    <TextField
                        label="CUI"
                        value={aguData.cui}
                        onChange={handleAGUChange('cui')}
                        fullWidth
                        style={{ marginBottom: '16px' }}
                    />
                    <TextField
                        label="Name"
                        value={aguData.name}
                        onChange={handleAGUChange('name')}
                        fullWidth
                        style={{ marginBottom: '16px' }}
                    />
                    <TextField
                        label="Min Level"
                        value={aguData.minLevel}
                        onChange={handleAGUChange('minLevel')}
                        fullWidth
                        style={{ marginBottom: '16px' }}
                    />
                    <TextField
                        label="Max Level"
                        value={aguData.maxLevel}
                        onChange={handleAGUChange('maxLevel')}
                        fullWidth
                        style={{ marginBottom: '16px' }}
                    />
                    <TextField
                        label="Critical Level"
                        value={aguData.criticalLevel}
                        onChange={handleAGUChange('criticalLevel')}
                        fullWidth
                        style={{ marginBottom: '16px' }}
                    />
                </Grid>
                <Grid item xs={6}>
                    <TextField
                        label="Load Volume"
                        value={aguData.loadVolume}
                        onChange={handleAGUChange('loadVolume')}
                        fullWidth
                        style={{ marginBottom: '16px' }}
                    />
                    <TextField
                        label="Latitude"
                        value={aguData.latitude}
                        onChange={handleAGUChange('latitude')}
                        fullWidth
                        style={{ marginBottom: '16px' }}
                    />
                    <TextField
                        label="Longitude"
                        value={aguData.longitude}
                        onChange={handleAGUChange('longitude')}
                        fullWidth
                        style={{ marginBottom: '16px' }}
                    />
                    <TextField
                        label="Location Name"
                        value={aguData.locationName}
                        onChange={handleAGUChange('locationName')}
                        fullWidth
                        style={{ marginBottom: '16px' }}
                    />
                    <TextField
                        label="ORD"
                        value={aguData.locationName}
                        onChange={handleAGUChange('ord')}
                        fullWidth
                        style={{ marginBottom: '16px' }}
                    />
                </Grid>
                <Grid item xs={12}>
                    <Typography variant="h4" align="center" gutterBottom>
                        Tanks
                        <AddBoxOutlinedIcon onClick={addTank} style={{ marginLeft: '8px' }}/>
                    </Typography>
                    {tankData.map((tank, index) => (
                        <div key={index}>
                            <Typography variant="h6" gutterBottom>
                                Tank {index + 1}
                                <IndeterminateCheckBoxOutlinedIcon onClick={() => removeTank(index)} style={{ marginLeft: '8px' }}/>
                            </Typography>
                            <Grid container spacing={3}>
                                <Grid item xs={6}>
                                    <TextField
                                        label="Min Level"
                                        value={tank.minLevel}
                                        onChange={handleTankChange('minLevel', index)}
                                        fullWidth
                                        style={{ marginBottom: '16px' }}
                                    />
                                    <TextField
                                        label="Max Level"
                                        value={tank.maxLevel}
                                        onChange={handleTankChange('maxLevel', index)}
                                        fullWidth
                                        style={{ marginBottom: '16px' }}
                                    />
                                    <TextField
                                        label="Critical Level"
                                        value={tank.criticalLevel}
                                        onChange={handleTankChange('criticalLevel', index)}
                                        fullWidth
                                        style={{ marginBottom: '16px' }}
                                    />
                                </Grid>
                                <Grid item xs={6}>
                                    <TextField
                                        label="Load Volume"
                                        value={tank.loadVolume}
                                        onChange={handleTankChange('loadVolume', index)}
                                        fullWidth
                                        style={{ marginBottom: '16px' }}
                                    />
                                    <TextField
                                        label="Capacity"
                                        value={tank.capacity}
                                        onChange={handleTankChange('capacity', index)}
                                        fullWidth
                                        style={{ marginBottom: '16px' }}
                                    />
                                </Grid>
                            </Grid>
                        </div>
                    ))}
                </Grid>
                <Grid item xs={12}>
                    <Typography variant="h4" align="center" gutterBottom>
                        Contacts
                        <AddBoxOutlinedIcon onClick={addContact} style={{ marginLeft: '8px' }}/>
                    </Typography>
                    {contacts.map((contact, index) => (
                        <div key={index}>
                            <Typography variant="h6" gutterBottom>
                                Contact {index + 1}
                                <IndeterminateCheckBoxOutlinedIcon onClick={() => removeContact(index)} style={{ marginLeft: '8px' }}/>
                            </Typography>
                            <Grid container spacing={3}>
                                <Grid item xs={6}>
                                    <TextField
                                        label="Name"
                                        value={contact.name}
                                        onChange={handleContactChange('name', index)}
                                        fullWidth
                                        style={{ marginBottom: '16px' }}
                                    />
                                    <TextField
                                        label="Phone"
                                        value={contact.phone}
                                        onChange={handleContactChange('phone', index)}
                                        fullWidth
                                        style={{ marginBottom: '16px' }}
                                    />
                                </Grid>
                                <Grid item xs={6}>
                                    <TextField
                                        label="Tipo do contacto"
                                        value={aguData.locationName}
                                        onChange={handleContactChange('type', index)}
                                        fullWidth
                                        style={{ marginBottom: '16px' }}
                                    />
                                </Grid>
                            </Grid>
                        </div>
                    ))}
                </Grid>
                <Grid item xs={12} container justifyContent="center">
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
                        onClick={handleSubmit}
                    >
                        Submit
                        <KeyboardArrowRightIcon />
                    </Button>
                </Grid>
            </Grid>
        </Container>
    );
}
