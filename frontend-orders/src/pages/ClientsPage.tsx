import {useCallback, useEffect, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import {
    Box,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Paper,
    TextField,
    Typography,
} from '@mui/material';
import {DataGrid, type GridColDef} from '@mui/x-data-grid';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import EmailIcon from '@mui/icons-material/Email';

import {AuthService, ClientService} from '../services';
import type {Client, CreateClientRequest} from '../types';

export const ClientsPage = () => {
    const {companyId} = useParams();
    const navigate = useNavigate();

    const isAdmin: boolean = AuthService.isAuthenticated() && AuthService.isAdmin();

    const [rows, setRows] = useState<Client[]>([]);
    const [loading, setLoading] = useState(false);

    const [openModal, setOpenModal] = useState(false);
    const [formData, setFormData] = useState<CreateClientRequest>({
        companyId: companyId || '',
        name: '',
        email: '',
        phone: '',
        address: ''
    });

    const fetchClients = useCallback(async () => {
        if (!companyId) return;
        try {
            setLoading(true);
            const data = await ClientService.getAll(companyId);
            setRows(data || []);
        } catch (error) {
            console.error("Error loading clients", error);
        } finally {
            setLoading(false);
        }
    }, [companyId]);

    useEffect(() => {
        void fetchClients();
    }, [fetchClients]);

    const handleCreate = async () => {
        if (!formData.name.trim()) return alert("El nombre es obligatorio");

        try {
            await ClientService.create({
                ...formData,
                companyId: companyId!
            });

            setOpenModal(false);
            setFormData({...formData, name: '', email: '', phone: '', address: ''}); // Limpiar
            void fetchClients();
        } catch (error) {
            console.error("Error creating client", error);
            alert("Error creating client");
        }
    };

    const columns: GridColDef[] = [
        {field: 'name', headerName: 'Name', flex: 1, minWidth: 150},
        {
            field: 'email',
            headerName: 'Email',
            width: 200,
            renderCell: (params) => (
                params.value ? (
                    <Box sx={{display: 'flex', alignItems: 'center', gap: 1}}>
                        <EmailIcon fontSize="small" color="action"/>
                        {params.value}
                    </Box>
                ) : <span style={{color: '#aaa'}}>N/A</span>
            )
        },
        {field: 'phone', headerName: 'Phone', width: 150},
        {field: 'address', headerName: 'Address', flex: 1, minWidth: 200}
    ];

    return (
        <Box component="main" sx={{p: 3}}>
            <Box sx={{display: 'flex', alignItems: 'center', mb: 3, justifyContent: 'space-between'}}>
                <Box sx={{display: 'flex', alignItems: 'center'}}>
                    <Button
                        startIcon={<ArrowBackIcon/>}
                        onClick={() => navigate('/companies')}
                        sx={{mr: 2}}
                    >
                        Back
                    </Button>
                    <Typography variant="h4">Clients Management</Typography>
                </Box>
                {isAdmin &&
                    <Button
                        variant="contained"
                        startIcon={<PersonAddIcon/>}
                        onClick={() => setOpenModal(true)}
                    >
                        New Client
                    </Button>}
            </Box>

            <Paper sx={{height: 500, width: '100%'}}>
                <DataGrid
                    rows={rows}
                    columns={columns}
                    loading={loading}
                    initialState={{pagination: {paginationModel: {pageSize: 10}}}}
                    pageSizeOptions={[10, 20]}
                    disableRowSelectionOnClick
                />
            </Paper>

            <Dialog open={openModal} onClose={() => setOpenModal(false)} maxWidth="sm" fullWidth>
                <DialogTitle>Register New Client</DialogTitle>
                <DialogContent>
                    <Box sx={{mt: 1, display: 'flex', flexDirection: 'column', gap: 2}}>
                        <TextField
                            label="Full Name"
                            fullWidth
                            required
                            autoFocus
                            value={formData.name}
                            onChange={(e) => setFormData({...formData, name: e.target.value})}
                        />
                        <TextField
                            label="Email Address"
                            type="email"
                            fullWidth
                            value={formData.email}
                            onChange={(e) => setFormData({...formData, email: e.target.value})}
                        />
                        <TextField
                            label="Phone Number"
                            fullWidth
                            value={formData.phone}
                            onChange={(e) => setFormData({...formData, phone: e.target.value})}
                        />
                        <TextField
                            label="Physical Address"
                            fullWidth
                            multiline
                            rows={2}
                            value={formData.address}
                            onChange={(e) => setFormData({...formData, address: e.target.value})}
                        />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenModal(false)} color="secondary">Cancel</Button>
                    <Button onClick={handleCreate} variant="contained">Save Client</Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};