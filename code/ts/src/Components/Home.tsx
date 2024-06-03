import * as React from 'react';
import FavBar from './FavBar/FavBar';
import Menu from './Menu/Menu';

export default function Home({ darkMode }: { darkMode: boolean }) {
    return (
        <div>
            <FavBar />
            <Menu darkMode={darkMode}/>
        </div>
    );
}