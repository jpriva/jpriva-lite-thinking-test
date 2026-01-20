import {useCallback, useEffect, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import {
    Box,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    MenuItem,
    Paper,
    TextField,
    Typography
} from '@mui/material';
import AddCircleIcon from '@mui/icons-material/AddCircle';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';

import {AuthService, CategoryService, ProductService} from '../services';
import type {Category, Product} from '../types';
import {CURRENCIES} from '../types';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import {CurrencySelector} from "../components/molecules";
import {CreateProductDialog, PdfActionDialog, ProductsTable} from "../components/organisms";
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';

export const ProductsPage = () => {
    const {companyId} = useParams();
    const navigate = useNavigate();
    const [currencyCode, setCurrencyCode] = useState<string>('COP');

    const currentCurrency = CURRENCIES.find(c => c.code === currencyCode) || CURRENCIES[0];

    const isAdmin: boolean = AuthService.isAuthenticated() && AuthService.isAdmin();

    const [rows, setRows] = useState<Product[]>([]);
    const [loading, setLoading] = useState(false);

    const [openCreate, setOpenCreate] = useState(false);
    const [openPdf, setOpenPdf] = useState(false);
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

    return (
        <Box component="main" sx={{p: 3}}>
            <Box sx={{display: 'flex', alignItems: 'center', mb: 3, justifyContent: 'space-between'}}>
                <Box sx={{display: 'flex', alignItems: 'center'}}>
                    <Button startIcon={<ArrowBackIcon/>} onClick={() => navigate('/companies')} sx={{mr: 2}}>
                        Back
                    </Button>
                    <Typography variant="h4">Products Inventory</Typography>
                </Box>
                <CurrencySelector
                    value={currencyCode}
                    onChange={(newCode) => setCurrencyCode(newCode)}
                    fullWidth
                    variant="outlined"
                    margin="normal"
                    sx={{width: '300px'}}
                />

                <Button
                    variant="contained"
                    startIcon={<PictureAsPdfIcon/>}
                    onClick={() => setOpenPdf(true)}
                >
                    Get Pdf
                </Button>
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
                <ProductsTable
                    products={rows}
                    selectedCurrency={currentCurrency}
                    isAdmin={AuthService.isAdmin()}
                    isLoading={loading}
                    handleOpenPrice={handleOpenPrice}
                    handleOpenStock={handleOpenStock}
                />
            </Paper>

            <CreateProductDialog
                open={openCreate}
                onClose={() => setOpenCreate(false)}
                companyId={companyId}
                categories={categories}
                fetchProducts={fetchProducts}
            ></CreateProductDialog>
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
            <PdfActionDialog
                open={openPdf}
                onClose={() => setOpenPdf(false)}
                companyId={companyId}
            />
        </Box>
    );
};