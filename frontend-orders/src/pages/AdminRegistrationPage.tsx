import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Container, Alert } from '@mui/material';
import { UserRegistrationForm } from '../components/organisms/UserRegistrationForm';
import { AuthService } from '../services';

export const AdminRegistrationPage = () => {
    const navigate = useNavigate();
    const isAdmin = AuthService.isAdmin();

    useEffect(() => {
        if (!AuthService.isAuthenticated()) {
            navigate('/login');
        }
    }, [navigate]);

    if (!isAdmin) {
        return (
            <Container component="main" sx={{ mt: 4 }}>
                <Alert severity="error">
                    Access Denied: You do not have permissions to register new users.
                </Alert>
            </Container>
        );
    }

    return (
        <Container component="main">
            <UserRegistrationForm />
        </Container>
    );
};