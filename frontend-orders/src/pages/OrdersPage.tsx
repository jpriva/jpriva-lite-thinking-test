import { useEffect, useState } from 'react';
import { useParams, useNavigate, Navigate } from 'react-router-dom';
import {
    Box,
    Typography,
    Button,
    Paper,
    Chip,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow
} from '@mui/material';
import { DataGrid, type GridColDef, type GridRenderCellParams } from '@mui/x-data-grid';
import VisibilityIcon from '@mui/icons-material/Visibility';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import type { Order } from "../types";
import { OrderService } from "../services/order.service";
import AddCircleIcon from '@mui/icons-material/AddCircle';


export const OrdersPage = () => {
    const { companyId } = useParams();
    const navigate = useNavigate();
    const [rows, setRows] = useState<Order[]>([]);
    const [loading, setLoading] = useState(false);
    const [rowCount, setRowCount] = useState(0);
    const [paginationModel, setPaginationModel] = useState({
        page: 0,
        pageSize: 10,
    });

    const roles:string | null = localStorage.getItem('role');
    const isAdmin = roles?.includes('ADMIN');
    const [openModal, setOpenModal] = useState(false);
    const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);

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
                console.error("Error loading orders:", error);
            } finally {
                setLoading(false);
            }
        };

        void fetchOrders();
    }, [companyId, paginationModel]);

    const handleOpenModal = (order: Order) => {
        setSelectedOrder(order);
        setOpenModal(true);
    };

    const handleCloseModal = () => {
        setOpenModal(false);
        setSelectedOrder(null);
    };

    const columns: GridColDef[] = [
        { field: 'id', headerName: 'Order #', width: 90 },
        { field: 'clientName', headerName: 'Client', width: 180 },
        { field: 'orderDate', headerName: 'Date', width: 120 },
        {
            field: 'status',
            headerName: 'Status',
            width: 130,
            renderCell: (params: GridRenderCellParams) => {
                const status = params.value as string;
                let color: "default" | "primary" | "secondary" | "error" | "info" | "success" | "warning" = "default";

                if (status === 'COMPLETED') color = "success";
                if (status === 'PENDING') color = "warning";
                if (status === 'CANCELLED') color = "error";

                return <Chip label={status} color={color} size="small" variant="outlined" />;
            }
        },
        {
            field: 'totalAmount',
            headerName: 'Total',
            width: 150,
            renderCell: (params: GridRenderCellParams) => {
                return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' }).format(params.value as number);
            }
        },
        {
            field: 'actions',
            headerName: 'Actions',
            width: 150,
            renderCell: (params: GridRenderCellParams) => (
                <Button
                    startIcon={<VisibilityIcon />}
                    size="small"
                    onClick={(e) => {
                        e.stopPropagation();
                        handleOpenModal(params.row);
                    }}
                >
                    View Items
                </Button>
            )
        }
    ];

    if (!companyId) {
        return <Navigate to="/companies" replace />;
    }

    return (
        <Box sx={{ p: 3, height: '100%' }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                <Button
                    startIcon={<ArrowBackIcon />}
                    onClick={() => navigate('/companies')}
                    sx={{ mr: 2 }}
                >
                    Back
                </Button>
                <Typography variant="h4">
                    Orders for Company: {companyId}
                </Typography>
            </Box>
            {isAdmin &&
            <Box sx={{ mb: 2, display: 'flex', justifyContent: 'flex-end' }}>
                <Button
                    variant="contained"
                    startIcon={<AddCircleIcon />}
                    onClick={() => navigate(`/orders/${companyId}/create`)}
                >
                    Create New Order
                </Button>
            </Box>}

            <Paper sx={{ height: 500, width: '100%' }}>
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

            <Dialog open={openModal} onClose={handleCloseModal} maxWidth="md" fullWidth>
                <DialogTitle>
                    Order Details #{selectedOrder?.id}
                </DialogTitle>
                <DialogContent dividers>
                    {selectedOrder && (
                        <>
                            <Box sx={{ mb: 3, display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2 }}>
                                <Typography><b>Client:</b> {selectedOrder.clientName}</Typography>
                                <Typography><b>Address:</b> {selectedOrder.address}</Typography>
                                <Typography><b>Date:</b> {selectedOrder.orderDate}</Typography>
                                <Typography><b>Total:</b> ${selectedOrder.totalAmount.toLocaleString()}</Typography>
                            </Box>

                            <Typography variant="h6" gutterBottom>Items</Typography>
                            <TableContainer component={Paper} variant="outlined">
                                <Table size="small">
                                    <TableHead sx={{ bgcolor: '#f5f5f5' }}>
                                        <TableRow>
                                            <TableCell>Product Name</TableCell>
                                            <TableCell align="right">Unit Price</TableCell>
                                            <TableCell align="center">Quantity</TableCell>
                                            <TableCell align="right">Subtotal</TableCell>
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {selectedOrder.items && selectedOrder.items.map((item) => (
                                            <TableRow key={item.id}>
                                                <TableCell>{item.productName}</TableCell>
                                                <TableCell align="right">${item.unitPrice.toLocaleString()}</TableCell>
                                                <TableCell align="center">{item.quantity}</TableCell>
                                                <TableCell align="right">
                                                    ${(item.unitPrice * item.quantity).toLocaleString()}
                                                </TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            </TableContainer>
                        </>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseModal} variant="contained">Close</Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};