import * as React from 'react';
import { Box, Button } from '@mui/material';
import portugalMap from '../../assets/portugalMap.png';

export default function Map() {
    return (
        <Box sx={{ position: 'relative', width: '100%', height: '800px' }}>
            <Box
                sx={{
                    position: 'absolute',
                    top: 0,
                    left: 0,
                    width: '100%',
                    height: '100%',
                    backgroundImage: `url(${portugalMap})`,
                    backgroundSize: '90% 100%',
                    backgroundRepeat: 'no-repeat',
                }}
            />
            <Box sx={{ position: 'absolute', top: '50px', left: '100px' }}>
                <Button variant="contained" color="primary" onClick={() => console.log('Button clicked')}>
                    Button 1
                </Button>
            </Box>
        </Box>
    );
}
