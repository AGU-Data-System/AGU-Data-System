import * as React from 'react';
import { Typography, IconButton, Divider } from '@mui/material';
import StarIcon from '@mui/icons-material/Star';
import StarOutlineIcon from '@mui/icons-material/StarOutline';
import { useState } from "react";
import { ReturnButton } from "../Layouts/Buttons";
import { ContactOutputModel } from "../../services/agu/models/aguOutputModel";
import { aguService } from "../../services/agu/aguService";
import { Problem } from "../../utils/Problem";
import EditIcon from '@mui/icons-material/Edit';
import { useNavigate } from "react-router-dom";

export default function AguHeader(
    { aguOrd, aguName, aguMetres, aguCUI, contacts, aguIsFavorite }: { aguOrd: string, aguName: string, aguMetres: number, aguCUI: string, contacts: ContactOutputModel[], aguIsFavorite: boolean }
) {
    const [isFavorite, setIsFavorite] = useState<boolean>(aguIsFavorite);
    const [waitFetch, setWaitFetch] = useState<boolean>(false);
    const navigate = useNavigate();

    const handleToggleFavorite = () => {
        setWaitFetch(true);
        const updateFavourite = async () => {
            const updateAgu = await aguService.updateFavouriteOnAGU(aguCUI, !isFavorite);
            if (updateAgu.value instanceof Error) {
                console.log(updateAgu.value.message);
            } else if (updateAgu.value instanceof Problem) {
                console.log(updateAgu.value.detail);
            } else {
                setIsFavorite(!isFavorite);
            }
        }
        updateFavourite();
        setTimeout(() => {
            setWaitFetch(false);
        }, 1000);
    };

    const handleToggleEdit = () => {
        navigate(`/uag/${aguCUI}/edit`);
    }

    return (
        <div style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            padding: '16px',
            borderBottom: '1px solid #000000',
        }}>
            <div style={{
                flex: 1,
                textAlign: 'center',
                margin: '8px',
            }}>
                <Typography variant="subtitle1">ORD {aguOrd}</Typography>
                <Typography variant="h4">{aguName}</Typography>
                <Typography variant="body2">{`CUI: ${aguCUI}`}</Typography>
                <Typography variant="body2" sx={{display:'flex', justifyContent:'flex-end', marginRight:'10px'}}>{aguMetres} m<sup>3</sup></Typography>
            </div>

            <Divider orientation="vertical" flexItem />

            <div style={{
                flex: 2,
                textAlign: 'center',
                display: 'flex',
                flexDirection: 'row',
                margin: '8px',
            }}>
                {displayContacts(contacts, 'logistic')}
                {displayContacts(contacts, 'emergency')}
            </div>

            <div style={{
                textAlign: 'center',
                margin: '8px',
            }}>
                <IconButton onClick={handleToggleEdit} disabled={waitFetch}>
                    <EditIcon fontSize='large' sx={{color: 'rgb(255, 165, 0)'}}/>
                </IconButton>
                <IconButton onClick={handleToggleFavorite} disabled={waitFetch}>
                    {isFavorite ? <StarIcon fontSize='large' sx={{color: 'rgb(255, 165, 0)'}}/> :
                        <StarOutlineIcon fontSize='large' sx={{color: 'rgb(255, 165, 0)'}}/>}
                </IconButton>
                <ReturnButton/>
            </div>
        </div>
    );
}

function displayContacts(contacts: ContactOutputModel[], type: string) {
    return (
        <div style={{
            marginLeft: '16px',
            display: 'flex',
            flexDirection: 'row',
            alignItems: 'flex-start',
            textAlign: 'left',
        }}>
            <div>
                <Typography variant="subtitle2">Contactos de {type.charAt(0).toUpperCase() == 'L' ? "Logística": "Emergencia"}:</Typography>
            </div>
            <div style={{
                marginLeft: '6px',
                maxHeight: '60px',
                overflowY: 'auto',
            }}>
                {contacts.filter(contact => contact.type.toLowerCase() === type).map((contact, index) => (
                    <Typography key={index} variant="body2">{`${contact.name} (${contact.phone})`}</Typography>
                ))}
            </div>
        </div>
    )
}