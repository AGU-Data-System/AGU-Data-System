import * as React from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import markerIcon from '../../assets/location_pin.png';
import * as L from 'leaflet';
import { Link } from 'react-router-dom';

export default function LeafletMap() {
    const markers: {geocode: [number, number], popUp: string}[] = [
        {
            geocode: [41.33134454715996, -6.968427934882252],
            popUp: 'Alfândega da Fé',
        },
        {
            geocode: [41.29741788010863, -7.477619913492303],
            popUp: 'Alijó',
        },
        {
            geocode: [41.24742767159777, -7.317666075364068],
            popUp: 'Cazerrada de Ansiães',
        },
    ];

    const icon = new L.Icon({
        iconUrl: markerIcon,
        iconSize: [25, 25],
    });

    return (
        <MapContainer center={[39.483068778739025, -8.09333730633312]} zoom={7} scrollWheelZoom={true} sx={{}}>
            <TileLayer
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            {markers.map((marker, index) => (
                <Marker key={index} position={marker.geocode as [number, number]} icon={icon}>
                    <Popup>
                        <div className="popup_tipologia">
                            UAG
                        </div>
                        <div className="popup_name">
                            {marker.popUp}
                        </div>
                        <br/>
                        <div className="popup_button">
                            <Link to={`/uag/${encodeURIComponent(marker.popUp)}`}>
                                Detalhes
                            </Link>
                        </div>
                    </Popup>
                </Marker>
            ))}
        </MapContainer>
    );
}
