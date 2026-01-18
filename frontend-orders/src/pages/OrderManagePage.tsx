import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
    Box, Paper, Typography, TextField, Button, Grid, MenuItem,
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Card, CardContent, Divider, Chip
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import AddShoppingCartIcon from '@mui/icons-material/AddShoppingCart';

import { OrderService } from '../services/order.service';
import { ProductService } from '../services/product.service';
import type { Order, Product } from '../types';

export const OrderManagePage = () => {
    const { companyId, orderId } = useParams();
    const navigate = useNavigate();

    const [order, setOrder] = useState<Order | null>(null);
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState(false);

    const [itemForm, setItemForm] = useState({
        productId: '',
        quantity: 1
    });

    useEffect(() => {
        const initData = async () => {
            if (!orderId || !companyId) return;
            try {
                const orderData = await OrderService.getById(orderId);
                setOrder(orderData);

                const prodData = await ProductService.getAll(companyId);
                setProducts(prodData || []);
            } catch (error) {
                console.error("Error loading data", error);
            }
        };
        void initData();
    }, [orderId, companyId]);

    const handleAddItem = async () => {
        if (!itemForm.productId) return alert("Select Product");
        if (itemForm.quantity <= 0) return alert("Invalid quantity");
        if (!orderId) return;

        try {
            setLoading(true);

            const updatedOrder = await OrderService.addItem(orderId, {
                productId: itemForm.productId,
                quantity: Number(itemForm.quantity)
            });

            setOrder(updatedOrder);

            setItemForm({ productId: '', quantity: 1 });
        } catch (error) {
            console.error("Error adding item", error);
            alert("Error adding item");
        } finally {
            setLoading(false);
        }
    };

    const getSelectedProductPrice = () => {
        if (!itemForm.productId || !order) return null;
        const prod = products.find(p => p.id === itemForm.productId);
        const price = prod?.prices?.[order.currency];
        return price ? `$${price}` : 'Price is not available.';
    };

    if (!order) return <Typography>Loading order...</Typography>;

    return (
        <Box sx={{ p: 3, maxWidth: 1000, margin: '0 auto' }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                <Button startIcon={<ArrowBackIcon />} onClick={() => navigate(`/orders/${companyId}`)} sx={{ mr: 2 }}>
                    Terminate / Go back
                </Button>
                <Typography variant="h4">Manage Order</Typography>
                <Chip label={order.status} color="primary" variant="outlined" sx={{ ml: 2 }} />
            </Box>

            <Card variant="outlined" sx={{ mb: 3, bgcolor: '#f8f9fa' }}>
                <CardContent>
                    <Grid container spacing={2}>
                        <Grid size={{ xs: 4 }}><Typography><b>Client:</b> {order.clientName}</Typography></Grid>
                        <Grid size={{ xs: 4 }}><Typography><b>Currency:</b> {order.currency}</Typography></Grid>
                        <Grid size={{ xs: 4 }}>
                            <Typography variant="h5" align="right" color="primary">
                                Total: {new Intl.NumberFormat('es-CO', { style: 'currency', currency: order.currency }).format(order.totalAmount)}
                            </Typography>
                        </Grid>
                    </Grid>
                </CardContent>
            </Card>

            <Divider sx={{ mb: 3 }}>Add Products</Divider>

            <Paper sx={{ p: 3, mb: 3 }}>
                <Grid container spacing={2} alignItems="center">

                    <Grid size={{ xs: 12, md: 6 }}>
                        <TextField
                            select
                            label="Select product"
                            fullWidth
                            value={itemForm.productId}
                            onChange={(e) => setItemForm({...itemForm, productId: e.target.value})}
                        >
                            {products.map((prod) => (
                                <MenuItem key={prod.id} value={prod.id}>
                                    {prod.sku} - {prod.name} (Stock: {prod.stockQuantity})
                                </MenuItem>
                            ))}
                        </TextField>
                        {itemForm.productId && (
                            <Typography variant="caption" color="text.secondary">
                                Unit price: {getSelectedProductPrice()}
                            </Typography>
                        )}
                    </Grid>

                    <Grid size={{ xs: 6, md: 3 }}>
                        <TextField
                            label="Cantidad"
                            type="number"
                            fullWidth
                            value={itemForm.quantity}
                            onChange={(e) => setItemForm({...itemForm, quantity: Number(e.target.value)})}
                            slotProps={{
                                htmlInput: { min: 1 }
                            }}
                        />
                    </Grid>

                    <Grid size={{ xs: 6, md: 3 }}>
                        <Button
                            variant="contained"
                            fullWidth
                            size="large"
                            startIcon={<AddShoppingCartIcon />}
                            onClick={handleAddItem}
                            disabled={loading || !itemForm.productId}
                        >
                            {loading ? "Adding..." : "Add"}
                        </Button>
                    </Grid>
                </Grid>
            </Paper>

            <TableContainer component={Paper} variant="outlined">
                <Table>
                    <TableHead sx={{ bgcolor: '#eee' }}>
                        <TableRow>
                            <TableCell>Product</TableCell>
                            <TableCell align="right">Unit Price</TableCell>
                            <TableCell align="center">Cant.</TableCell>
                            <TableCell align="right">Subtotal</TableCell>
                            {/* <TableCell align="center">Action</TableCell> */}
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {order.items.map((item) => (
                            <TableRow key={item.id}>
                                <TableCell>{item.productName}</TableCell>
                                <TableCell align="right">
                                    {new Intl.NumberFormat('es-CO', { style: 'currency', currency: order.currency }).format(item.unitPrice)}
                                </TableCell>
                                <TableCell align="center">{item.quantity}</TableCell>
                                <TableCell align="right">
                                    {new Intl.NumberFormat('es-CO', { style: 'currency', currency: order.currency }).format(item.unitPrice * item.quantity)}
                                </TableCell>
                            </TableRow>
                        ))}
                        {order.items.length === 0 && (
                            <TableRow>
                                <TableCell colSpan={5} align="center" sx={{ py: 3 }}>
                                    Order is empty. Add products above.
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    );
};