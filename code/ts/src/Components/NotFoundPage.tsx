import { Box, Button, } from '@mui/material';
import * as React from 'react';
import Astronaut from '../assets/confusedAstronaut.png';
import { useNavigate } from 'react-router-dom';

export default function NotFoundPage() {
  const navigate = useNavigate();
  return (
    <Box
      sx={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        backgroundImage: `url(${Astronaut})`,
        backgroundSize: 'cover',
        backgroundRepeat: 'no-repeat',
        backgroundPosition: 'center',
      }}
    >
      <Button variant="contained" style={{ maxWidth: '140px', maxHeight: '70px' }} color="error" onClick={() => navigate('/')}>
        Fly Home
      </Button>
    </Box>
  );
}