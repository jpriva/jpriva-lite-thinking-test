import React, {useState} from 'react';
import {Button, Paper, Typography} from '@mui/material';
import {FormInput} from '../atoms';
import {UserRoleSelector} from '../molecules';
import {AuthService} from '../../services';
import type {CreateUserRequest} from "../../types";

export const UserRegistrationForm = () => {
    const [formData, setFormData] = useState({
        email: '',
        password: '',
        fullName: '',
        phone: '',
        address: '',
        role: 'USER'
    });

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const newUser: CreateUserRequest = formData as CreateUserRequest;
            await AuthService.register(newUser);
            alert('User created successfully');
            setFormData({email: '', password: '', fullName: '', phone: '', address: '', role: 'EXTERNAL'});
        } catch (error) {
            console.error('Registration failed', error);
        }
    };

    return (
        <Paper sx={{p: 4, maxWidth: 500, mx: 'auto', mt: 4}}>
            <Typography variant="h5" gutterBottom>Register New User</Typography>
            <form onSubmit={handleSubmit}>
                <FormInput
                    label="Full Name"
                    value={formData.fullName}
                    onChange={(e) => setFormData({...formData, fullName: e.target.value})}
                />
                <FormInput
                    label="Email Address"
                    type="email"
                    value={formData.email}
                    onChange={(e) => setFormData({...formData, email: e.target.value})}
                />
                <FormInput
                    label="Phone Number"
                    type="tel"
                    value={formData.phone}
                    onChange={(e) => setFormData({...formData, phone: e.target.value})}
                />
                <FormInput
                    label="Address"
                    type="text"
                    value={formData.address}
                    onChange={(e) => setFormData({...formData, address: e.target.value})}
                />
                <FormInput
                    label="Password"
                    type="password"
                    value={formData.password}
                    onChange={(e) => setFormData({...formData, password: e.target.value})}
                />
                <UserRoleSelector
                    value={formData.role}
                    onChange={(role) => setFormData({...formData, role})}
                />
                <Button type="submit" variant="contained" fullWidth sx={{mt: 2}}>
                    Create User
                </Button>
            </form>
        </Paper>
    );
};