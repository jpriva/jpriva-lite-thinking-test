import React, { useState } from 'react';
import {
    Box,
    Button,
    Card,
    CardContent,
    TextField,
    Typography,
    Alert
} from '@mui/material';
import axiosClient from '../api/axiosClient';
import type {TokenResponse} from "../types";
import {jwtDecode} from "jwt-decode";

export const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');

        try {
            const response = await axiosClient.post('/auth/login', {
                email,
                password
            });

            const tokenResponse: TokenResponse = response.data;
            const token: string = tokenResponse.accessToken;
            console.log(tokenResponse);
            console.log(token);
            const decoded: any = jwtDecode(token);
            console.log(decoded)
            localStorage.setItem('token', token);
            const userRole: string = decoded.roles || decoded.authorities || "EXTERNAL";
            localStorage.setItem('role', userRole);
            window.location.href = '/companies';

        } catch (err) {
            console.error(err);
            setError('Error');
        }
    };

    return (
        <Box
            sx={{
                height: '100vh',
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                backgroundColor: '#f5f5f5'
            }}
        >
            <Card sx={{ maxWidth: 400, width: '100%', p: 2, boxShadow: 3 }}>
                <CardContent>
                    <Typography variant="h5" component="div" gutterBottom sx={{ textAlign: 'center' }}>
                        LOGIN
                    </Typography>

                    <Typography variant="body2" color="text.secondary" sx={{ textAlign: 'center', mb: 3 }}>
                        Orders
                    </Typography>

                    <form onSubmit={handleLogin}>

                        <TextField
                            label="Email"
                            type="email"
                            fullWidth
                            margin="normal"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />

                        <TextField
                            label="Password"
                            type="password"
                            fullWidth
                            margin="normal"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />

                        {}
                        {error && (
                            <Alert severity="error" sx={{ mt: 2 }}>
                                {error}
                            </Alert>
                        )}

                        <Button
                            type="submit"
                            variant="contained"
                            fullWidth
                            size="large"
                            sx={{ mt: 3 }}
                        >
                            Login
                        </Button>
                    </form>
                </CardContent>
            </Card>
        </Box>
    );
};