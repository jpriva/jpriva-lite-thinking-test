import {useEffect, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import {
    Box,
    Button,
    Chip,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    IconButton,
    MenuItem,
    Paper,
    TextField,
    Tooltip,
    Typography
} from '@mui/material';
import {DataGrid, type GridColDef, type GridRenderCellParams} from '@mui/x-data-grid';
import AddCircleIcon from '@mui/icons-material/AddCircle';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import VisibilityIcon from '@mui/icons-material/Visibility';

import {AuthService, ClientService, OrderService} from '../services';
import {type Client, CURRENCIES, type Order} from '../types';

export const OrdersPage = () => {
    const {companyId} = useParams();
    const navigate = useNavigate();

    const [rows, setRows] = useState<Order[]>([]);
    const [loading, setLoading] = useState(false);
    const [rowCount, setRowCount] = useState(0);
    const [paginationModel, setPaginationModel] = useState({
        page: 0,
        pageSize: 10,
    });

    const [openCreateModal, setOpenCreateModal] = useState(false);
    const [clients, setClients] = useState<Client[]>([]);
    const [newOrderForm, setNewOrderForm] = useState({
        clientId: '',
        currencyCode: 'COP'
    });

    const isAdmin: boolean = AuthService.isAuthenticated() && AuthService.isAdmin();

    useEffect(() => {
        const fetchOrders = async () => {
            if (!companyId) return;
            try {
                setLoading(true);
                const data = await OrderService.getAll(
                    companyId,
                    paginationModel.page,
                    paginationModel.pageSize
                );

                setRows(data.content);
                setRowCount(data.totalElements);
            } catch (error) {
                console.error(error);
            } finally {
                setLoading(false);
            }
        };
        void fetchOrders();
    }, [companyId, paginationModel]);

    useEffect(() => {
        const fetchClients = async () => {
            if (!companyId) return;
            try {
                const data = await ClientService.getAll(companyId);
                setClients(data || []);
            } catch (e) {
                console.error(e);
            }
        };
        void fetchClients();
    }, [companyId]);

    const handleCreateHeader = async () => {
        if (!newOrderForm.clientId) return alert("Please select a client");
        if (!companyId) return;

        try {
            const newOrder = await OrderService.create({
                companyId,
                clientId: newOrderForm.clientId,
                currencyCode: newOrderForm.currencyCode
            });

            setOpenCreateModal(false);
            navigate(`/orders/${companyId}/manage/${newOrder.id}`);

        } catch (error) {
            console.error(error);
            alert("Error creating order");
        }
    };

    const columns: GridColDef[] = [
        {
            field: 'id',
            headerName: 'Order ID',
            width: 100,
            renderCell: (params) => params.value.substring(0, 8)
        },
        {field: 'clientName', headerName: 'Client', flex: 1, minWidth: 150},
        {field: 'orderDate', headerName: 'Date', width: 150},
        {
            field: 'totalAmount',
            headerName: 'Total',
            width: 180,
            renderCell: (params: GridRenderCellParams) => {
                const order = params.row as Order;
                return (
                    <Typography variant="body2" fontWeight="bold">
                        {new Intl.NumberFormat('en-US', {
                            style: 'currency',
                            currency: order.currency
                        }).format(order.totalAmount)}
                    </Typography>
                );
            }
        },
        {
            field: 'status',
            headerName: 'Status',
            width: 120,
            renderCell: (params) => {
                const status = params.value as string;
                let color: 'default' | 'primary' | 'success' | 'warning' = 'default';
                if (status === 'PENDING') color = 'warning';
                if (status === 'COMPLETED') color = 'success';

                return <Chip label={status} color={color} size="small" variant="outlined"/>;
            }
        },
        {
            field: 'actions',
            headerName: 'Manage',
            width: 100,
            sortable: false,
            renderCell: (params: GridRenderCellParams) => (
                <Tooltip title="View & Edit Items">
                    <IconButton
                        color="primary"
                        onClick={() => navigate(`/orders/${companyId}/manage/${params.row.id}`)}
                    >
                        <VisibilityIcon/>
                    </IconButton>
                </Tooltip>
            )
        }
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
                    <Typography variant="h4">Orders</Typography>
                </Box>
                {isAdmin &&
                    <Button
                        variant="contained"
                        startIcon={<AddCircleIcon/>}
                        onClick={() => setOpenCreateModal(true)}
                    >
                        New Order
                    </Button>}
            </Box>

            <Paper sx={{height: 500, width: '100%'}}>
                <DataGrid
                    rows={rows}
                    columns={columns}
                    loading={loading}
                    rowCount={rowCount}
                    pageSizeOptions={[5, 10, 20]}
                    paginationMode="server"
                    paginationModel={paginationModel}
                    onPaginationModelChange={setPaginationModel}
                    disableRowSelectionOnClick
                />
            </Paper>

            <Dialog open={openCreateModal} onClose={() => setOpenCreateModal(false)} maxWidth="sm" fullWidth>
                <DialogTitle>Create New Order</DialogTitle>
                <DialogContent>
                    <Box sx={{mt: 2, display: 'flex', flexDirection: 'column', gap: 3}}>

                        <TextField
                            select
                            label="Select Client"
                            fullWidth
                            value={newOrderForm.clientId}
                            onChange={(e) => setNewOrderForm({...newOrderForm, clientId: e.target.value})}
                        >
                            {clients.length > 0 ? (
                                clients.map((client) => (
                                    <MenuItem key={client.id} value={client.id}>
                                        {client.name}
                                    </MenuItem>
                                ))
                            ) : (
                                <MenuItem disabled value="">No clients found</MenuItem>
                            )}
                        </TextField>

                        <TextField
                            select
                            label="Currency"
                            fullWidth
                            value={newOrderForm.currencyCode}
                            onChange={(e) => setNewOrderForm({...newOrderForm, currencyCode: e.target.value})}
                        >
                            {CURRENCIES.map((curr) => (
                                <MenuItem key={curr.code} value={curr.code}>
                                    {curr.code} - {curr.name} ({curr.symbol})
                                </MenuItem>
                            ))}
                        </TextField>

                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenCreateModal(false)} color="secondary">Cancel</Button>
                    <Button onClick={handleCreateHeader} variant="contained">
                        Next: Add Items
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};