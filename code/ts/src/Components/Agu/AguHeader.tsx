import * as React from 'react';
import { Typography, IconButton, Divider } from '@mui/material';
import StarIcon from '@mui/icons-material/Star';
import StarOutlineIcon from '@mui/icons-material/StarOutline';
import {useState} from "react";
import { ReturnButton } from "../Layouts/Buttons";
import {ContactOutputModel} from "../../services/agu/models/aguOutputModel";

export default function AguHeader(
    { aguOrd, aguName, aguMetres, aguCUI, contacts, aguIsFavorite }: { aguOrd: string, aguName: string, aguMetres: number, aguCUI: string, contacts: ContactOutputModel[], aguIsFavorite: boolean }
) {
    const [isFavorite, setIsFavorite] = useState<boolean>(aguIsFavorite);
    const [waitFetch, setWaitFetch] = useState<boolean>(false);

    const handleToggleFavorite = () => {
        setWaitFetch(true);
        setIsFavorite(!isFavorite);
        setTimeout(() => {
            setWaitFetch(false);
        }, 1000);
    };

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
                <Typography variant="subtitle1">{aguOrd}</Typography>
                <Typography variant="h4">{aguName}</Typography>
                <Typography variant="body2" sx={{display:'flex', justifyContent:'flex-end', marginRight:'10px'}}>{aguMetres} m<sup>3</sup></Typography>
            </div>

            <Divider orientation="vertical" flexItem />

            <div style={{
                flex: 2,
                textAlign: 'center',
                margin: '8px',
            }}>
                {displayContacts(contacts, 'logistic')}
                {displayContacts(contacts, 'emergency')}
                <div style={{
                    marginLeft: '16px',
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'flex-start',
                }}>
                    <Typography variant="body2">{`CUI: ${aguCUI}`}</Typography>
                </div>
            </div>

            <div style={{
                textAlign: 'center',
                margin: '8px',
            }}>
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
                <Typography variant="subtitle2">{type.charAt(0).toUpperCase() + type.slice(1)} Contacts:</Typography>
            </div>
            <div style={{
                marginLeft: '6px',
                maxHeight: '50px',
                overflowY: 'auto',
            }}>
                {contacts.filter(contact => contact.type.toLowerCase() === type).map((contact, index) => (
                    <Typography key={index} variant="body2">{`${contact.name} (${contact.phone})`}</Typography>
                ))}
            </div>
        </div>
    )
}