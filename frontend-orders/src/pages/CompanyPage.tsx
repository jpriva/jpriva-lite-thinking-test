import React, { useEffect, useState } from 'react';
import {DataGrid, type GridColDef, type GridRowParams} from '@mui/x-data-grid';
import type { Company } from '../types';
import { CompanyService } from '../services/company.service';
import {
    Box,
    Button,
    Typography,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField
} from '@mui/material';
import {useNavigate} from "react-router-dom";

const columns: GridColDef[] = [
    { field: 'taxId', headerName: 'NIT/ID', width: 150 },
    { field: 'name', headerName: 'Name', width: 200 },
    { field: 'address', headerName: 'Address', width: 250 },
    { field: 'phone', headerName: 'Phone', width: 150 },
];

export const CompanyPage = () => {
    const navigate = useNavigate();
    const [rows, setRows] = useState<Company[]>([]);
    const [loading, setLoading] = useState(true);
    const roles:string | null = localStorage.getItem('roles');
    const isAdmin = roles?.includes('ADMIN');

    const [open, setOpen] = useState(false);
    const [formData, setFormData] = useState({
        taxId: '',
        name: '',
        address: '',
        phone: ''
    });

    const fetchCompanies = async () => {
        try {
            setLoading(true);
            const data = await CompanyService.getAll();
            const rowsWithId = data.map(row => ({ ...row, id: row.taxId }));
            setRows(rowsWithId);
        } catch (error) {
            console.error("Error loading companies", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCompanies();
    }, []);

    const handleOpen = () => setOpen(true);

    const handleClose = () => {
        setOpen(false);
        setFormData({ taxId: '', name: '', address: '', phone: '' });
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSave = async () => {
        try {
            await CompanyService.create(formData);
            await fetchCompanies();
            handleClose();
        } catch (error) {
            console.error("Error creating company", error);
            alert("Error creating company. Please try again.");
        }
    };

    const handleRowClick = (params: GridRowParams) => {
        const companyId = params.row.taxId;
        navigate(`/orders/${companyId}`);
    };

    return (
        <Box sx={{ height: 400, width: '100%', p: 2 }}>
            <Typography variant="h4" gutterBottom>Companies</Typography>

            {isAdmin &&
            <Button variant="contained" onClick={handleOpen} sx={{ mb: 2 }}>
                New Company
            </Button>}

            <DataGrid
                rows={rows}
                columns={columns}
                loading={loading}
                onRowClick={handleRowClick}
                sx={{
                    '& .MuiDataGrid-row:hover': {
                        cursor: 'pointer',
                        backgroundColor: '#f5f5f5'
                    }
                }}
                initialState={{
                    pagination: { paginationModel: { pageSize: 10 } },
                }}
                pageSizeOptions={[5, 10]}
                disableRowSelectionOnClick
            />

            <Dialog open={open} onClose={handleClose}>
                <DialogTitle>Create new company</DialogTitle>
                <DialogContent>
                    <TextField
                        autoFocus
                        margin="dense"
                        name="taxId"
                        label="NIT / ID"
                        type="text"
                        fullWidth
                        variant="outlined"
                        value={formData.taxId}
                        onChange={handleChange}
                    />
                    <TextField
                        margin="dense"
                        name="name"
                        label="Nombre de la Empresa"
                        type="text"
                        fullWidth
                        variant="outlined"
                        value={formData.name}
                        onChange={handleChange}
                    />
                    <TextField
                        margin="dense"
                        name="address"
                        label="Dirección"
                        type="text"
                        fullWidth
                        variant="outlined"
                        value={formData.address}
                        onChange={handleChange}
                    />
                    <TextField
                        margin="dense"
                        name="phone"
                        label="Teléfono"
                        type="text"
                        fullWidth
                        variant="outlined"
                        value={formData.phone}
                        onChange={handleChange}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose} color="secondary">Cancel</Button>
                    <Button onClick={handleSave} variant="contained" color="primary">Save</Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};