import * as React from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import markerIcon from '../../assets/location_pin.png';
import * as L from 'leaflet';
import { Link } from 'react-router-dom';
import { useEffect, useState } from "react";
import { aguService } from "../../services/agu/aguService";
import { AgusBasicInfoOutputModel } from "../../services/agu/models/aguOutputModel";
import { Box, CircularProgress } from "@mui/material";
import Typography from "@mui/material/Typography";
import { MapError } from "../Layouts/Error";
import { Problem } from "../../utils/Problem";

const centerOfPortugal = [39.483068778739025, -8.09333730633312];

type MapState =
    | { type: 'loading' }
    | { type: 'error'; message: string }
    | { type: 'success'; uagsDetails: AgusBasicInfoOutputModel[] };

export default function LeafletMap({ filter, darkMode }: { filter: string, darkMode: boolean }) {
    const [state, setState] = useState<MapState>({ type: 'loading' });

    useEffect(() => {
        const fetchGetAGUs = async () => {
            const getAGUsResponse = await aguService.getAGUs();

            if (getAGUsResponse.value instanceof Error) {
                setState({
                    type: 'error',
                    message: getAGUsResponse.value.message,
                });
            } else if (getAGUsResponse.value instanceof Problem) {
                setState({ type: 'error', message: getAGUsResponse.value.title });
            } else {
                switch (filter.toLowerCase()) {
                    case 'norte':
                        setState({ type: 'success', uagsDetails: getAGUsResponse.value.agusBasicInfo.filter(uag => uag.dno.region.toLowerCase() === 'north') });
                        break;
                    case 'centro':
                        setState({ type: 'success', uagsDetails: getAGUsResponse.value.agusBasicInfo.filter(uag => uag.dno.region.toLowerCase() === 'center') });
                        break;
                    case 'sul':
                        setState({ type: 'success', uagsDetails: getAGUsResponse.value.agusBasicInfo.filter(uag => uag.dno.region.toLowerCase() === 'south') });
                        break;
                    case 'sng':
                        setState({ type: 'success', uagsDetails: getAGUsResponse.value.agusBasicInfo.filter(uag => uag.dno.name.toLowerCase() === 'sng') });
                        break;
                    case 'tgg':
                        setState({ type: 'success', uagsDetails: getAGUsResponse.value.agusBasicInfo.filter(uag => uag.dno.name.toLowerCase() === 'tgg') });
                        break;
                    case 'dur':
                        setState({ type: 'success', uagsDetails: getAGUsResponse.value.agusBasicInfo.filter(uag => uag.dno.name.toLowerCase() === 'dur') });
                        break;
                    case 'inactive':
                        setState({ type: 'success', uagsDetails: getAGUsResponse.value.agusBasicInfo.filter(uag => !uag.isActive) });
                        break;
                    default:
                        setState({ type: 'success', uagsDetails: getAGUsResponse.value.agusBasicInfo.filter(uag => uag.isActive) });
                }
            }
        }
        fetchGetAGUs();
    }, [filter]);

    const icon = new L.Icon({
        iconUrl: markerIcon,
        iconSize: [25, 25],
    });

    if (state.type === 'loading') {
        return (
            <Box sx={{
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center',
                alignItems: 'center',
                height: '100vh',
            }}>
                <Typography variant="h5" gutterBottom>Loading...</Typography>
                <CircularProgress sx={{ color: 'rgb(255, 165, 0)' }}/>
            </Box>
        );
    }

    if (state.type === 'error') {
        return (
            <MapError message={state.message}/>
        );
    }

    return (
        <MapContainer center={centerOfPortugal} zoom={7} scrollWheelZoom={true}>
            <TileLayer
                url={darkMode ? 'https://tiles.stadiamaps.com/tiles/alidade_smooth_dark/{z}/{x}/{y}{r}.png' : 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'}
            />
            {state.uagsDetails.map((marker, index) => (
                <Marker key={index} position={[marker.location.latitude, marker.location.longitude]} icon={icon}>
                    <Popup>
                        <div className="popup_tipologia">
                            ORD {marker.dno.name}
                        </div>
                        <div className="popup_name">
                            {marker.name}
                        </div>
                        <br/>
                        <div className="popup_button">
                            <Link to={`/uag/${encodeURIComponent(marker.cui)}`}>
                                Detalhes
                            </Link>
                        </div>
                    </Popup>
                </Marker>
            ))}
        </MapContainer>
    );
}
