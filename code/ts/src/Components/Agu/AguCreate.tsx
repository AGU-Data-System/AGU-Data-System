import * as React from 'react';
import { useState } from 'react';
import {Button, Container, Grid, MenuItem, TextField, Typography} from '@mui/material';
import AddBoxOutlinedIcon from '@mui/icons-material/AddBoxOutlined';
import IndeterminateCheckBoxOutlinedIcon from '@mui/icons-material/IndeterminateCheckBoxOutlined';
import { ReturnButton } from "../Layouts/Buttons";
import KeyboardArrowRightIcon from "@mui/icons-material/KeyboardArrowRight";
import { aguService } from "../../services/agu/aguService";
import { useNavigate } from "react-router-dom";
import { AguCreateInputModel, AguCreateWithoutTanksAndContactsModel, ContactCreateInputModel, TankCreateInputModel } from "../../services/agu/models/createAguInputModel";
import {Problem} from "../../utils/Problem";

const contact_types = [
    {
        value: 'logistic',
        label: 'Logistic',
    },
    {
        value: 'emergency',
        label: 'Emergency',
    },
];

const initialAGUData: AguCreateWithoutTanksAndContactsModel = {
    cui: '',
    name: '',
    minLevel: '',
    maxLevel: '',
    criticalLevel: '',
    loadVolume: '',
    latitude: '',
    longitude: '',
    locationName: '',
    dnoName: '',
    gasLevelUrl: '',
    image: '',
    isFavorite: false,
    notes: '',
};

export default function AguCreate() {
    const navigate = useNavigate();

    const [tankData, setTankData] = useState<TankCreateInputModel[]>([]);
    const [contacts, setContacts] = useState<ContactCreateInputModel[]>([]);
    const [aguData, setAGUData] = useState<AguCreateWithoutTanksAndContactsModel>(initialAGUData);

    const handleAGUChange = (prop: keyof typeof aguData) => (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        setAGUData({ ...aguData, [prop]: event.target.value });
    };

    const handleTankChange = (prop: keyof TankCreateInputModel, index: number) => (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const newTanks = [...tankData];
        newTanks[index][prop] = parseInt(event.target.value, 10);
        setTankData(newTanks);
    };

    const handleContactChange = (prop: keyof ContactCreateInputModel, index: number) => (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const newContacts = [...contacts];
        newContacts[index][prop] = event.target.value;
        setContacts(newContacts);
    };

    const addTank = () => {
        setTankData([...tankData, {
            number: tankData.length + 1,
            minLevel: 20,
            maxLevel: 80,
            criticalLevel: 10,
            loadVolume: 20,
            capacity: 100,
        }]);
    };

    const addContact = () => {
        setContacts([...contacts, {
            name: '',
            phone: '',
            type: '',
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
        const fetchCreateAGU = async () => {
            const aguDataInput: AguCreateInputModel = {
                cui: aguData.cui,
                name: aguData.name,
                minLevel: parseInt(aguData.minLevel, 10),
                maxLevel: parseInt(aguData.maxLevel, 10),
                criticalLevel: parseInt(aguData.criticalLevel, 10),
                loadVolume: parseInt(aguData.loadVolume, 10),
                latitude: parseInt(aguData.latitude, 10),
                longitude: parseInt(aguData.longitude, 10),
                locationName: aguData.locationName,
                dnoName: aguData.dnoName,
                gasLevelUrl: aguData.gasLevelUrl,
                image: aguData.image,
                tanks: tankData,
                contacts: contacts,
                isFavorite: false,
                notes: '',
            }
            console.log('AGU data:', aguDataInput);
            const createAGUResponse = await aguService.createAgu(aguDataInput);

            if (createAGUResponse.value instanceof Error) {
                console.error('Error creating AGU:', createAGUResponse);
                return;
            } else if (createAGUResponse.value instanceof Problem) {
                console.error('Error creating AGU:', createAGUResponse.value.title);
                return;
            } else {
                console.log('AGU created:', createAGUResponse);

                setAGUData(initialAGUData);

                setTankData([]);
                setContacts([]);

                // Show success message
                alert('AGU created successfully!');

                // Redirect to AGU details page
                navigate(`/uag/${createAGUResponse.value.cui}`);
            }
        }

        fetchCreateAGU().then(
            () => console.log('AGU created successfully!'),
            (error) => console.error('Error creating AGU:', error)
        )
    };

    return (
        <Container style={{ marginTop: '40px' }}>
            <div style={{display: 'flex', justifyContent: 'flex-end'}}>
                <ReturnButton/>
            </div>
            <Typography variant="h4" align="center" gutterBottom>
                AGU Details
            </Typography>
            <form>
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
                            type="number"
                            value={aguData.minLevel}
                            onChange={handleAGUChange('minLevel')}
                            fullWidth
                            style={{ marginBottom: '16px' }}
                        />
                        <TextField
                            label="Max Level"
                            type="number"
                            value={aguData.maxLevel}
                            onChange={handleAGUChange('maxLevel')}
                            fullWidth
                            style={{ marginBottom: '16px' }}
                        />
                        <TextField
                            label="Critical Level"
                            type="number"
                            value={aguData.criticalLevel}
                            onChange={handleAGUChange('criticalLevel')}
                            fullWidth
                            style={{ marginBottom: '16px' }}
                        />
                        <TextField
                            label="Gas URL"
                            value={aguData.gasLevelUrl}
                            onChange={handleAGUChange('gasLevelUrl')}
                            fullWidth
                            style={{ marginBottom: '16px' }}
                        />
                    </Grid>
                    <Grid item xs={6}>
                        <TextField
                            label="Load Volume"
                            type="number"
                            value={aguData.loadVolume}
                            onChange={handleAGUChange('loadVolume')}
                            fullWidth
                            style={{ marginBottom: '16px' }}
                        />
                        <TextField
                            label="Latitude"
                            type="number"
                            value={aguData.latitude}
                            onChange={handleAGUChange('latitude')}
                            fullWidth
                            style={{ marginBottom: '16px' }}
                        />
                        <TextField
                            label="Longitude"
                            type="number"
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
                            value={aguData.dnoName}
                            onChange={handleAGUChange('dnoName')}
                            fullWidth
                            style={{ marginBottom: '16px' }}
                        />
                        <TextField
                            label="Image"
                            value={aguData.image}
                            onChange={handleAGUChange('image')}
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
                                            type="number"
                                            value={tank.minLevel}
                                            onChange={handleTankChange('minLevel', index)}
                                            fullWidth
                                            style={{ marginBottom: '16px' }}
                                        />
                                        <TextField
                                            label="Max Level"
                                            type="number"
                                            value={tank.maxLevel}
                                            onChange={handleTankChange('maxLevel', index)}
                                            fullWidth
                                            style={{ marginBottom: '16px' }}
                                        />
                                        <TextField
                                            label="Critical Level"
                                            type="number"
                                            value={tank.criticalLevel}
                                            onChange={handleTankChange('criticalLevel', index)}
                                            fullWidth
                                            style={{ marginBottom: '16px' }}
                                        />
                                    </Grid>
                                    <Grid item xs={6}>
                                        <TextField
                                            label="Load Volume"
                                            type="number"
                                            value={tank.loadVolume}
                                            onChange={handleTankChange('loadVolume', index)}
                                            fullWidth
                                            style={{ marginBottom: '16px' }}
                                        />
                                        <TextField
                                            label="Capacity"
                                            type="number"
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
                                            select
                                            value={contact.type}
                                            onChange={handleContactChange('type', index)}
                                            fullWidth
                                            style={{ marginBottom: '16px' }}
                                        >
                                            {contact_types.map((option) => (
                                                <MenuItem key={option.value} value={option.value}>
                                                    {option.label}
                                                </MenuItem>
                                            ))}
                                        </TextField>
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
            </form>
        </Container>
    );
}
