import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
    Box,
    Paper,
    Typography,
    Button,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
} from '@mui/material';
import { DataGrid, type GridColDef } from '@mui/x-data-grid';
import AddCircleIcon from '@mui/icons-material/AddCircle';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';

import { CategoryService } from '../services/category.service';
import type { Category, CreateCategoryRequest } from '../types';

export const CategoriesPage = () => {
    const { companyId } = useParams();
    const navigate = useNavigate();

    const roles:string | null = localStorage.getItem('role');
    const isAdmin = roles?.includes('ADMIN');

    const [rows, setRows] = useState<Category[]>([]);
    const [loading, setLoading] = useState(false);

    const [openModal, setOpenModal] = useState(false);
    const [formData, setFormData] = useState({
        name: '',
        description: ''
    });

    const fetchCategories = async () => {
        if (!companyId) return;
        try {
            setLoading(true);
            const data = await CategoryService.getAll(companyId);
            setRows(data || []);
        } catch (error) {
            console.error("Error loading categories", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        void fetchCategories();
    }, [companyId]);


    const handleCreate = async () => {
        if (!formData.name.trim()) {
            alert("Name is required. Please enter a valid name for the category.");
            return;
        }

        try {
            const payload: CreateCategoryRequest = {
                companyId: companyId!,
                name: formData.name,
                description: formData.description
            };

            await CategoryService.create(payload);

            setOpenModal(false);
            setFormData({ name: '', description: '' });
            void fetchCategories();
        } catch (error) {
            console.error("Error creating category", error);
            alert("Error creating category");
        }
    };

    const columns: GridColDef[] = [
        { field: 'name', headerName: 'Name', width: 250, editable: true },
        { field: 'description', headerName: 'Description', width: 400 },
    ];

    return (
        <Box component="main" sx={{ p: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 3, justifyContent: 'space-between' }}>
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    <Button
                        startIcon={<ArrowBackIcon />}
                        onClick={() => navigate('/companies')}
                        sx={{ mr: 2 }}
                    >
                        Back
                    </Button>
                    <Typography variant="h4">Categories</Typography>
                </Box>

                {isAdmin &&
                <Button
                    variant="contained"
                    startIcon={<AddCircleIcon />}
                    onClick={() => setOpenModal(true)}
                >
                    New Category
                </Button>}
            </Box>

            <Paper sx={{ height: 500, width: '100%' }}>
                <DataGrid
                    rows={rows}
                    columns={columns}
                    loading={loading}
                    initialState={{ pagination: { paginationModel: { pageSize: 10 } } }}
                    pageSizeOptions={[10, 20]}
                    disableRowSelectionOnClick
                />
            </Paper>

            <Dialog open={openModal} onClose={() => setOpenModal(false)} maxWidth="sm" fullWidth>
                <DialogTitle>Add New Category</DialogTitle>
                <DialogContent>
                    <Box sx={{ mt: 1, display: 'flex', flexDirection: 'column', gap: 2 }}>
                        <TextField
                            autoFocus
                            label="Category Name"
                            fullWidth
                            required
                            value={formData.name}
                            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                        />
                        <TextField
                            label="Description"
                            fullWidth
                            multiline
                            rows={3}
                            value={formData.description}
                            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                        />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenModal(false)} color="secondary">Cancel</Button>
                    <Button onClick={handleCreate} variant="contained">Save</Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};