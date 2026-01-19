import {useCallback, useEffect, useState} from 'react';
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
    Stack,
    TextField,
    Tooltip,
    Typography
} from '@mui/material';
import {DataGrid, type GridColDef, type GridRenderCellParams} from '@mui/x-data-grid';
import AddCircleIcon from '@mui/icons-material/AddCircle';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';

import {AuthService, CategoryService, ProductService} from '../services';
import type {Category, CreateProductRequest, Product} from '../types';
import {CURRENCIES} from '../types';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';

export const ProductsPage = () => {
    const {companyId} = useParams();
    const navigate = useNavigate();

    const isAdmin: boolean = AuthService.isAuthenticated() && AuthService.isAdmin();

    const [rows, setRows] = useState<Product[]>([]);
    const [loading, setLoading] = useState(false);

    const [openCreate, setOpenCreate] = useState(false);
    const [newProduct, setNewProduct] = useState<CreateProductRequest>({
        companyId: companyId || '',
        categoryId: '',
        name: '',
        sku: '',
        description: ''
    });
    const [categories, setCategories] = useState<Category[]>([]);
    const [openStockModal, setOpenStockModal] = useState(false);
    const [stockAmount, setStockAmount] = useState<number | string>('');
    const [selectedProductId, setSelectedProductId] = useState<string | null>(null);

    const [openPriceModal, setOpenPriceModal] = useState(false);
    const [priceData, setPriceData] = useState({
        price: '',
        currencyCode: 'COP'
    });

    const fetchProducts = useCallback(async () => {
        if (!companyId) return;
        try {
            setLoading(true);
            const data = await ProductService.getAll(companyId);
            setRows(data || []);
        } catch (error) {
            console.error("Error loading products", error);
        } finally {
            setLoading(false);
        }
    }, [companyId]);

    const fetchCategories = useCallback(async () => {
        if (!companyId) return;
        try {
            const data = await CategoryService.getAll(companyId);
            setCategories(data || []);
        } catch (error) {
            console.error("Error loading categories", error);
        }
    }, [companyId]);

    useEffect(() => {
        void fetchProducts();
        void fetchCategories();
    }, [fetchProducts, fetchCategories]);

    const handleCreate = async () => {
        if (!newProduct.name || !newProduct.sku || !newProduct.categoryId) {
            alert("Name, SKU and Category ID are required");
            return;
        }
        try {
            await ProductService.create(newProduct);
            setOpenCreate(false);
            setNewProduct({...newProduct, name: '', sku: '', description: ''});
            void fetchProducts();
        } catch (error) {
            console.error("Error creating product", error);
            alert("Error creating product");
        }
    };

    const handleOpenStock = (id: string) => {
        setSelectedProductId(id);
        setStockAmount('');
        setOpenStockModal(true);
    };

    const handleSaveStock = async () => {
        if (!selectedProductId || !stockAmount || Number(stockAmount) <= 0) {
            alert("Value not valid. Please enter a positive number greater than 0.");
            return;
        }

        try {
            await ProductService.increaseStock(selectedProductId, Number(stockAmount));

            setOpenStockModal(false);
            void fetchProducts();
        } catch (error) {
            console.error("Error updating stock", error);
            alert("Error updating stock");
        }
    };

    const handleOpenPrice = (id: string) => {
        setSelectedProductId(id);
        setPriceData({price: '', currencyCode: 'COP'});
        setOpenPriceModal(true);
    };

    const handleSavePrice = async () => {
        if (!selectedProductId || !priceData.price || Number(priceData.price) <= 0) {
            alert("Invalid price. Please enter a positive number greater than 0.");
            return;
        }

        try {
            await ProductService.updatePrice(selectedProductId, {
                price: Number(priceData.price),
                currencyCode: priceData.currencyCode
            });

            setOpenPriceModal(false);
            void fetchProducts();
        } catch (error) {
            console.error("Error updating price", error);
            alert("Error updating price");
        }
    };

    const columns: GridColDef[] = [
        {field: 'sku', headerName: 'SKU', width: 120, minWidth: 100},
        {field: 'name', headerName: 'Product Name', flex: 1.5, minWidth: 150},
        {
            field: 'stockQuantity',
            headerName: 'Stock',
            width: 130,
            renderCell: (params) => (
                <Box sx={{display: 'flex', alignItems: 'center', gap: 1}}>
                    <Typography
                        fontWeight="bold"
                        color={params.value < 10 ? 'error.main' : 'success.main'}
                    >
                        {params.value}
                    </Typography>

                    {isAdmin &&
                        <Tooltip title="Add Stock">
                            <IconButton
                                size="small"
                                color="primary"
                                onClick={(e) => {
                                    e.stopPropagation();
                                    handleOpenStock(params.row.id);
                                }}
                            >
                                <AddIcon fontSize="small"/>
                            </IconButton>
                        </Tooltip>}
                </Box>
            )
        },
        {
            field: 'prices',
            headerName: 'Prices',
            flex: 2,
            minWidth: 250,
            renderCell: (params: GridRenderCellParams) => {
                const pricesMap = params.value as Record<string, number>;

                return (
                    <Box sx={{
                        display: 'flex',
                        alignItems: 'center',
                        height: '100%',
                        width: '100%',
                        overflow: 'hidden'
                    }}>
                        <Stack
                            direction="row"
                            spacing={1}
                            sx={{
                                overflowX: 'auto',
                                width: '100%',
                                pb: '4px',
                                '&::-webkit-scrollbar': {height: '6px'},
                                '&::-webkit-scrollbar-thumb': {backgroundColor: '#e0e0e0', borderRadius: '10px'}
                            }}
                        >
                            {pricesMap && Object.entries(pricesMap).map(([code, amount]) => {
                                const currencyInfo = CURRENCIES.find(c => c.code === code);

                                const symbol = currencyInfo ? currencyInfo.symbol : code;

                                const tooltipText = currencyInfo
                                    ? `${currencyInfo.code} - ${currencyInfo.name}`
                                    : code;

                                return (
                                    <Tooltip key={code} title={tooltipText} arrow placement="top">
                                        <Chip
                                            label={`${symbol} ${amount.toLocaleString()}`}
                                            size="small"
                                            variant="outlined"
                                            sx={{backgroundColor: '#fff', cursor: 'help'}}
                                        />
                                    </Tooltip>
                                );
                            })}
                        </Stack>
                        {isAdmin &&
                            <Tooltip title="Update Price">
                                <IconButton
                                    size="small"
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        handleOpenPrice(params.row.id);
                                    }}
                                >
                                    <EditIcon fontSize="small"/>
                                </IconButton>
                            </Tooltip>}
                    </Box>
                );
            }
        },
        {field: 'description', headerName: 'Description', flex: 1, minWidth: 150},
    ];

    return (
        <Box component="main" sx={{p: 3}}>
            <Box sx={{display: 'flex', alignItems: 'center', mb: 3, justifyContent: 'space-between'}}>
                <Box sx={{display: 'flex', alignItems: 'center'}}>
                    <Button startIcon={<ArrowBackIcon/>} onClick={() => navigate('/companies')} sx={{mr: 2}}>
                        Back
                    </Button>
                    <Typography variant="h4">Products Inventory</Typography>
                </Box>
                {isAdmin &&
                    <Button
                        variant="contained"
                        startIcon={<AddCircleIcon/>}
                        onClick={() => setOpenCreate(true)}
                    >
                        New Product
                    </Button>}
            </Box>

            <Paper sx={{height: 500, width: '100%'}}>
                <DataGrid
                    rows={rows}
                    columns={columns}
                    loading={loading}
                    getRowId={(row) => row.id}
                    initialState={{pagination: {paginationModel: {pageSize: 10}}}}
                    pageSizeOptions={[10, 20]}
                    disableRowSelectionOnClick
                />
            </Paper>

            <Dialog open={openCreate} onClose={() => setOpenCreate(false)}>
                <DialogTitle>Register New Product</DialogTitle>
                <DialogContent>
                    <Box sx={{mt: 1, display: 'flex', flexDirection: 'column', gap: 2, minWidth: 400}}>
                        <TextField
                            label="Name"
                            fullWidth
                            value={newProduct.name}
                            onChange={(e) => setNewProduct({...newProduct, name: e.target.value})}
                        />
                        <TextField
                            label="SKU (Unique Code)"
                            fullWidth
                            value={newProduct.sku}
                            onChange={(e) => setNewProduct({...newProduct, sku: e.target.value})}
                        />
                        <TextField
                            select
                            label="Category"
                            fullWidth
                            value={newProduct.categoryId}
                            onChange={(e) => setNewProduct({...newProduct, categoryId: e.target.value})}
                            helperText="Select the category for this product"
                        >
                            {categories.length > 0 ? (
                                categories.map((cat) => (
                                    <MenuItem key={cat.id} value={cat.id}>
                                        {cat.name}
                                    </MenuItem>
                                ))
                            ) : (
                                <MenuItem value="" disabled>
                                    No categories found. Create one first!
                                </MenuItem>
                            )}
                        </TextField>
                        <TextField
                            label="Description"
                            fullWidth
                            multiline
                            rows={3}
                            value={newProduct.description}
                            onChange={(e) => setNewProduct({...newProduct, description: e.target.value})}
                        />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenCreate(false)} color="secondary">Cancel</Button>
                    <Button onClick={handleCreate} variant="contained">Save</Button>
                </DialogActions>
            </Dialog>
            <Dialog open={openStockModal} onClose={() => setOpenStockModal(false)} maxWidth="xs" fullWidth>
                <DialogTitle>Add Stock</DialogTitle>
                <DialogContent>
                    <Typography variant="body2" sx={{mb: 2}}>
                        Enter the quantity to add to the existing inventory.
                    </Typography>
                    <TextField
                        autoFocus
                        label="Quantity to Add"
                        type="number"
                        fullWidth
                        value={stockAmount}
                        onChange={(e) => setStockAmount(e.target.value)}
                        slotProps={{
                            htmlInput: {min: 1}
                        }}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenStockModal(false)} color="secondary">Cancel</Button>
                    <Button onClick={handleSaveStock} variant="contained" startIcon={<AddIcon/>}>
                        Add
                    </Button>
                </DialogActions>
            </Dialog>
            <Dialog open={openPriceModal} onClose={() => setOpenPriceModal(false)} maxWidth="xs" fullWidth>
                <DialogTitle>Update Price</DialogTitle>
                <DialogContent>
                    <Box sx={{mt: 1, display: 'flex', flexDirection: 'column', gap: 2}}>

                        <TextField
                            select
                            label="Currency"
                            fullWidth
                            value={priceData.currencyCode}
                            onChange={(e) => setPriceData({...priceData, currencyCode: e.target.value})}
                        >
                            {CURRENCIES.map((option) => (
                                <MenuItem key={option.code} value={option.code}>
                                    {/* Se verá así: "USD - US Dollar ($)" */}
                                    {option.code} - {option.name} ({option.symbol})
                                </MenuItem>
                            ))}
                        </TextField>

                        <TextField
                            autoFocus
                            label="New Price"
                            type="number"
                            fullWidth
                            value={priceData.price}
                            onChange={(e) => setPriceData({...priceData, price: e.target.value})}
                            slotProps={{
                                htmlInput: {min: 0}
                            }}
                        />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenPriceModal(false)} color="secondary">Cancel</Button>
                    <Button onClick={handleSavePrice} variant="contained" startIcon={<EditIcon/>}>
                        Update
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};