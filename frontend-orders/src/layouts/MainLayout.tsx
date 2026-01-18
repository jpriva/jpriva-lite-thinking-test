import { Outlet, Navigate } from 'react-router-dom';
import { Navbar } from '../components/Navbar';
import { Box } from '@mui/material';

export const MainLayout = () => {
    const isAuthenticated = !!localStorage.getItem('token');

    if (!isAuthenticated) {
        return <Navigate to="/login" />;
    }

    return (
        <Box sx={{ minHeight: '100vh', bgcolor: '#f5f5f5' }}>
            <Navbar />

            <Box sx={{ p: 2 }}>
                <Outlet />
            </Box>
        </Box>
    );
};