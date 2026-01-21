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
    Typography
} from '@mui/material';
import AddCircleIcon from '@mui/icons-material/AddCircle';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';

import {AuthService, CategoryService, ProductService} from '../services';
import {type Category, type Currency, DEFAULT_CURRENCY, getCurrencySafe, type Product} from '../types';
import AddIcon from '@mui/icons-material/Add';
import {CurrencySelector} from "../components/molecules";
import {CreateProductDialog, PdfActionDialog, ProductsTable, UpdateProductPriceDialog} from "../components/organisms";
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';

export const ProductsPage = () => {
    const {companyId} = useParams();
    const navigate = useNavigate();

    const [currencyCode, setCurrencyCode] = useState<string>(DEFAULT_CURRENCY.code);
    const currentCurrency:Currency = getCurrencySafe(currencyCode);

    const isAdmin: boolean = AuthService.isAuthenticated() && AuthService.isAdmin();

    const [rows, setRows] = useState<Product[]>([]);
    const [loading, setLoading] = useState(false);

    const [openCreate, setOpenCreate] = useState(false);
    const [openPdf, setOpenPdf] = useState(false);
    const [categories, setCategories] = useState<Category[]>([]);

    const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
    const [stockAmount, setStockAmount] = useState<number | string>('');


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


    const [openStockModal, setOpenStockModal] = useState(false);
    const handleOpenStock = (product: Product) => {
        setSelectedProduct(product);
        setStockAmount('');
        setOpenStockModal(true);
    };
    const handleSaveStock = async () => {
        if (!selectedProduct || !stockAmount || Number(stockAmount) <= 0) {
            alert("Value not valid. Please enter a positive number greater than 0.");
            return;
        }

        try {
            await ProductService.increaseStock(selectedProduct.id, Number(stockAmount));

            setOpenStockModal(false);
            void fetchProducts();
        } catch (error) {
            console.error("Error updating stock", error);
            alert("Error updating stock");
        }
    };

    const [openPriceModal, setOpenPriceModal] = useState(false);
    const [editingData, setEditingData] = useState({ price: '', currencyCode: '' });
    const handleOpenPrice = (product: Product) => {
        setSelectedProduct(product);
        setEditingData({
            price: product.prices[currentCurrency.code]?.toString() || '0',
            currencyCode: currentCurrency.code
        });
        setOpenPriceModal(true);
    };
    const handleEditPriceChangeCurrency = (currencyCode: string) => {
        setEditingData({
            price: selectedProduct?.prices[currencyCode]?.toString() || '0',
            currencyCode: currencyCode
        });
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
            <UpdateProductPriceDialog
                priceData={editingData}
                setPriceData={setEditingData}
                open={openPriceModal}
                onClose={()=>setOpenPriceModal(false)}
                onChangeCurrency={handleEditPriceChangeCurrency}
                companyId={companyId}
                selectedProduct={selectedProduct!}
                initialCurrency={currentCurrency}
                fetchProducts={fetchProducts}
            />
            <PdfActionDialog
                open={openPdf}
                onClose={() => setOpenPdf(false)}
                companyId={companyId}
            />
        </Box>
    );
};