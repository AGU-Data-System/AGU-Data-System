import * as React from 'react';
import { useEffect, useState } from 'react';
import { useParams } from "react-router-dom";
import { aguService } from "../../services/agu/aguService";
import { Problem } from "../../utils/Problem";
import { Button, TextField, List, ListItem, ListItemText, IconButton, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Snackbar, Alert } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import { AguDetailsOutputModel, ContactInputModel, TankInputModel } from "../../services/agu/models/aguOutputModel";
import EditIcon from "@mui/icons-material/Edit";
import {AddButton, BackToAguDetailsButton, EditButton} from "../Layouts/Buttons";

type AguState =
    | { type: 'loading' }
    | { type: 'error'; message: string }
    | { type: 'success'; aguDetails: AguDetailsOutputModel };

export default function AguEdit() {
    const { aguId } = useParams<{ aguId: string }>();
    const [state, setState] = useState<AguState>({ type: 'loading' });
    const [newContact, setNewContact] = useState<ContactInputModel>({ name: '', phone: '', type: '' });
    const [newTank, setNewTank] = useState<TankInputModel>({
        number: 0,
        minLevel: 0,
        maxLevel: 0,
        criticalLevel: 0,
        capacity: 0,
        correctionFactor: 0
    });
    const [levelValues, setLevelValues] = useState({ min: 0, max: 0, critical: 0 });
    const [confirmOpen, setConfirmOpen] = useState(false);
    const [itemToDelete, setItemToDelete] = useState<{ type: 'contact' | 'tank'; id: number } | null>(null);
    const [snackbarMessage, setSnackbarMessage] = useState('');
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarSeverity, setSnackbarSeverity] = useState<'success' | 'error'>('success');
    const [editTankNumber, setEditTankNumber] = useState<number | null>(null);
    const [editTankData, setEditTankData] = useState<TankInputModel>(newTank);

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
            } else if (getAguDetails.value instanceof Problem) {
                setState({ type: 'error', message: getAguDetails.value.title });
            } else {
                const details = getAguDetails.value;
                setState({ type: 'success', aguDetails: details });
                setLevelValues({ min: details.levels.min, max: details.levels.max, critical: details.levels.critical });
                setNewTank({
                    number: details.tanks.size + 1,
                    minLevel: 0,
                    maxLevel: 0,
                    criticalLevel: 0,
                    capacity: 0,
                    correctionFactor: 0
                });
            }
        };
        fetch();
    }, [aguId]);

    const handleAddContact = async () => {
        if (aguId === undefined) {
            return;
        }
        if (state.type === 'success') {
            const updatedContacts = await aguService.addContact(aguId, newContact);
            if (updatedContacts.value instanceof Error) {
                setSnackbarMessage(updatedContacts.value.message);
                setSnackbarSeverity('error');
            } else if (updatedContacts.value instanceof Problem) {
                setSnackbarMessage(updatedContacts.value.title);
                setSnackbarSeverity('error');
            } else {
                setState({
                    ...state,
                    aguDetails: {
                        ...state.aguDetails,
                        contacts: {
                            contacts: [...state.aguDetails.contacts.contacts, {
                                id: updatedContacts.value,
                                name: newContact.name,
                                phone: newContact.phone,
                                type: newContact.type
                            }],
                            size: state.aguDetails.contacts.size + 1
                        }
                    }
                });
                setNewContact({ name: '', phone: '', type: '' });
                setSnackbarMessage('Contact added successfully');
                setSnackbarSeverity('success');
            }
            setSnackbarOpen(true);
        }
    };

    const handleDeleteContact = async (contactName: number) => {
        if (aguId === undefined) {
            return;
        }
        if (state.type === 'success') {
            const updatedContacts = await aguService.deleteContact(aguId, contactName);
            if (updatedContacts.value instanceof Error) {
                setSnackbarMessage(updatedContacts.value.message);
                setSnackbarSeverity('error');
            } else if (updatedContacts.value instanceof Problem) {
                setSnackbarMessage(updatedContacts.value.title);
                setSnackbarSeverity('error');
            } else {
                setState({
                    ...state,
                    aguDetails: { ...state.aguDetails, contacts: { contacts: state.aguDetails.contacts.contacts.filter(contact => contact.id !== contactName), size: state.aguDetails.contacts.size - 1 } }
                });
                setSnackbarMessage('Contact removed successfully');
                setSnackbarSeverity('success');
            }
            setSnackbarOpen(true);
        }
    };

    const handleAddTank = async () => {
        if (aguId === undefined) {
            return;
        }
        if (state.type === 'success') {
            const updatedTanks = await aguService.addTank(aguId, newTank);
            if (updatedTanks.value instanceof Error) {
                setSnackbarMessage(updatedTanks.value.message);
                setSnackbarSeverity('error');
            } else if (updatedTanks.value instanceof Problem) {
                setSnackbarMessage(updatedTanks.value.title);
                setSnackbarSeverity('error');
            } else {
                setState({
                    ...state,
                    aguDetails: {
                        ...state.aguDetails,
                        tanks: {
                            tanks: [...state.aguDetails.tanks.tanks, {
                                number: updatedTanks.value.number,
                                levels: {
                                    min: newTank.minLevel,
                                    max: newTank.maxLevel,
                                    critical: newTank.criticalLevel
                                },
                                capacity: newTank.capacity,
                                correctionFactor: newTank.correctionFactor
                            }],
                            size: state.aguDetails.tanks.size + 1
                        }
                    }
                });
                setNewTank({
                    number: newTank.number + 1,
                    minLevel: 0,
                    maxLevel: 0,
                    criticalLevel: 0,
                    capacity: 0,
                    correctionFactor: 0
                });
                setSnackbarMessage('Tank added successfully');
                setSnackbarSeverity('success');
            }
            setSnackbarOpen(true);
        }
    };

    const handleLevelsChange = async () => {
        if (aguId === undefined) {
            return;
        }
        if (state.type === 'success') {
            const updatedAgu = await aguService.updateAguLevels(aguId, levelValues);
            if (updatedAgu.value instanceof Error) {
                setSnackbarMessage(updatedAgu.value.message);
                setSnackbarSeverity('error');
            } else if (updatedAgu.value instanceof Problem) {
                setSnackbarMessage(updatedAgu.value.title);
                setSnackbarSeverity('error');
            } else {
                setState({
                    ...state,
                    aguDetails: updatedAgu.value
                });
                setSnackbarMessage('Levels updated successfully');
                setSnackbarSeverity('success');
            }
            setSnackbarOpen(true);
        }
    };

    const handleEditTankClick = (tank: TankInputModel) => {
        setEditTankNumber(tank.number);
        setEditTankData(tank);
    };

    const handleUpdateTank = async () => {
        if (aguId === undefined) {
            return;
        }
        if (state.type === 'success' && editTankNumber !== null) {
            const updatedAgu = await aguService.updateTank(aguId, editTankData.number, editTankData);
            if (updatedAgu.value instanceof Error) {
                setSnackbarMessage(updatedAgu.value.message);
                setSnackbarSeverity('error');
            } else if (updatedAgu.value instanceof Problem) {
                setSnackbarMessage(updatedAgu.value.title);
                setSnackbarSeverity('error');
            } else {
                setState({
                    ...state,
                    aguDetails: updatedAgu.value
                });
                setSnackbarMessage('Tank updated successfully');
                setSnackbarSeverity('success');
                setEditTankNumber(null);
            }
            setSnackbarOpen(true);
        }
    };

    const openConfirmDialog = (type: 'contact' | 'tank', id: number) => {
        setItemToDelete({ type, id });
        setConfirmOpen(true);
    };

    const handleConfirmDelete = () => {
        if (itemToDelete) {
            if (itemToDelete.type === 'contact') {
                handleDeleteContact(itemToDelete.id);
            } else if (itemToDelete.type === 'tank') {
                // handleDeleteTank(itemToDelete.id);
            }
            setConfirmOpen(false);
            setItemToDelete(null);
        }
    };

    const handleCloseConfirmDialog = () => {
        setConfirmOpen(false);
        setItemToDelete(null);
    };

    const handleSnackbarClose = () => {
        setSnackbarOpen(false);
    };

    return (
        <div>
            {state.type === 'loading' && <p>Loading...</p>}
            {state.type === 'error' && <p>Error: {state.message}</p>}
            {state.type === 'success' && (
                <div>
                    <h1>Editando {state.aguDetails.name}... <BackToAguDetailsButton aguCUI={state.aguDetails.cui}/></h1>
                    <h3>Contacts</h3>
                    <List>
                        {state.aguDetails.contacts.contacts.map(contact => (
                            <ListItem key={contact.id}>
                                <ListItemText primary={contact.name} secondary={`${contact.phone} (${contact.type})`} />
                                <IconButton onClick={() => openConfirmDialog('contact', contact.id)}>
                                    <DeleteIcon fontSize='large' sx={{color: 'rgb(255, 165, 0)'}}/>
                                </IconButton>
                            </ListItem>
                        ))}
                    </List>
                    <TextField
                        label="Name"
                        value={newContact.name}
                        onChange={e => setNewContact({ ...newContact, name: e.target.value })}
                    />
                    <TextField
                        label="Phone"
                        value={newContact.phone}
                        onChange={e => setNewContact({ ...newContact, phone: e.target.value })}
                    />
                    <TextField
                        label="Type"
                        value={newContact.type}
                        onChange={e => setNewContact({ ...newContact, type: e.target.value })}
                    />
                    <AddButton handleClick={handleAddContact} />
                    <h3>Tanks</h3>
                    <List>
                        {state.aguDetails.tanks.tanks.map(tank => (
                            <ListItem key={tank.number}>
                                <ListItemText
                                    primary={`Tank ${tank.number}`}
                                    secondary={`Min: ${tank.levels.min}, Max: ${tank.levels.max}, Critical: ${tank.levels.critical}, Capacity: ${tank.capacity}, Correction Factor: ${tank.correctionFactor}`}
                                />
                                <IconButton onClick={() => handleEditTankClick(
                                    {
                                        number: tank.number,
                                        minLevel: tank.levels.min,
                                        maxLevel: tank.levels.max,
                                        criticalLevel: tank.levels.critical,
                                        capacity: tank.capacity,
                                        correctionFactor: tank.correctionFactor
                                    }
                                )}>
                                    <EditIcon fontSize='large' sx={{color: 'rgb(255, 165, 0)'}}/>
                                </IconButton>
                                <IconButton onClick={() => openConfirmDialog('tank', tank.number)}>
                                    <DeleteIcon fontSize='large' sx={{color: 'rgb(255, 165, 0)'}}/>
                                </IconButton>
                                {editTankNumber === tank.number && (
                                    <div>
                                        <TextField
                                            label="Min Level"
                                            value={editTankData.minLevel}
                                            onChange={e => setEditTankData({ ...editTankData, minLevel: Number(e.target.value) })}
                                        />
                                        <TextField
                                            label="Max Level"
                                            value={editTankData.maxLevel}
                                            onChange={e => setEditTankData({ ...editTankData, maxLevel: Number(e.target.value) })}
                                        />
                                        <TextField
                                            label="Critical Level"
                                            value={editTankData.criticalLevel}
                                            onChange={e => setEditTankData({ ...editTankData, criticalLevel: Number(e.target.value) })}
                                        />
                                        <TextField
                                            label="Capacity"
                                            value={editTankData.capacity}
                                            onChange={e => setEditTankData({ ...editTankData, capacity: Number(e.target.value) })}
                                        />
                                        <TextField
                                            label="Correction Factor"
                                            value={editTankData.correctionFactor}
                                            onChange={e => setEditTankData({ ...editTankData, correctionFactor: Number(e.target.value) })}
                                        />
                                        <EditButton handleClick={handleUpdateTank} />
                                    </div>
                                )}
                            </ListItem>
                        ))}
                    </List>
                    <TextField
                        label="Number"
                        value={newTank.number}
                        disabled={true}
                    />
                    <TextField
                        label="Min Level"
                        value={newTank.minLevel}
                        onChange={e => setNewTank({ ...newTank, minLevel: Number(e.target.value) })}
                    />
                    <TextField
                        label="Max Level"
                        value={newTank.maxLevel}
                        onChange={e => setNewTank({ ...newTank, maxLevel: Number(e.target.value) })}
                    />
                    <TextField
                        label="Critical Level"
                        value={newTank.criticalLevel}
                        onChange={e => setNewTank({ ...newTank, criticalLevel: Number(e.target.value) })}
                    />
                    <TextField
                        label="Capacity"
                        value={newTank.capacity}
                        onChange={e => setNewTank({ ...newTank, capacity: Number(e.target.value) })}
                    />
                    <TextField
                        label="Correction Factor"
                        value={newTank.correctionFactor}
                        onChange={e => setNewTank({ ...newTank, correctionFactor: Number(e.target.value) })}
                    />
                    <AddButton handleClick={handleAddTank} />
                    <h3>Level Values</h3>
                    <TextField
                        label="Min"
                        value={levelValues.min}
                        onChange={e => setLevelValues({ ...levelValues, min: Number(e.target.value) })}
                    />
                    <TextField
                        label="Max"
                        value={levelValues.max}
                        onChange={e => setLevelValues({ ...levelValues, max: Number(e.target.value) })}
                    />
                    <TextField
                        label="Critical"
                        value={levelValues.critical}
                        onChange={e => setLevelValues({ ...levelValues, critical: Number(e.target.value) })}
                    />
                    <EditButton handleClick={handleLevelsChange} />
                    <Dialog
                        open={confirmOpen}
                        onClose={handleCloseConfirmDialog}
                    >
                        <DialogTitle>Confirm Deletion</DialogTitle>
                        <DialogContent>
                            <DialogContentText>Are you sure you want to delete this item?</DialogContentText>
                        </DialogContent>
                        <DialogActions>
                            <Button onClick={handleCloseConfirmDialog}>Cancel</Button>
                            <Button onClick={handleConfirmDelete}>Delete</Button>
                        </DialogActions>
                    </Dialog>
                    <Snackbar
                        open={snackbarOpen}
                        autoHideDuration={6000}
                        onClose={handleSnackbarClose}
                    >
                        <Alert onClose={handleSnackbarClose} severity={snackbarSeverity}>
                            {snackbarMessage}
                        </Alert>
                    </Snackbar>
                </div>
            )}
        </div>
    );
}