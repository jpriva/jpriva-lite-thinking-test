import {CURRENCIES, type Currency, getCurrencySafe, type Product} from "../../types";
import {Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, MenuItem, TextField} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import {ProductService} from "../../services";
import type {Dispatch, SetStateAction} from "react";
import {PriceEditor} from "../atoms";

type UpdateProductPriceDialogProps = {
    priceData: {
        price: string;
        currencyCode: string;
    };
    setPriceData: Dispatch<SetStateAction<{ price: string; currencyCode: string }>>;
    open: boolean;
    onClose: () => void;
    onChangeCurrency: (currencyCode: string) => void;
    companyId: string | undefined;
    selectedProduct: Product;
    initialCurrency: Currency;
    fetchProducts: () => Promise<void>;
}

export const UpdateProductPriceDialog = ({
                                             open,
                                             priceData,
                                             setPriceData,
                                             onClose,
                                             onChangeCurrency,
                                             selectedProduct,
                                             fetchProducts
                                         }: UpdateProductPriceDialogProps) => {


    const handleSavePrice = async () => {
        if (!selectedProduct || !priceData.price || Number(priceData.price) <= 0) {
            alert("Invalid price. Please enter a positive number greater than 0.");
            return;
        }

        try {
            await ProductService.updatePrice(selectedProduct.id, {
                price: Number(priceData.price),
                currencyCode: priceData.currencyCode
            });

            onClose();
            void fetchProducts();
        } catch (error) {
            console.error("Error updating price", error);
            alert("Error updating price");
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
            <DialogTitle>Update Price</DialogTitle>
            <DialogContent>
                <Box sx={{mt: 1, display: 'flex', flexDirection: 'column', gap: 2}}>

                    <TextField
                        select
                        label="Currency"
                        fullWidth
                        value={priceData.currencyCode}
                        onChange={(e) => onChangeCurrency(e.target.value)}
                    >
                        {CURRENCIES.map((option) => (
                            <MenuItem key={option.code} value={option.code}>
                                {option.code} - {option.name} ({option.symbol})
                            </MenuItem>
                        ))}
                    </TextField>
                    <PriceEditor
                        autoFocus
                        currency={getCurrencySafe(priceData.currencyCode)}
                        value={priceData.price}
                        onChange={(val) => setPriceData({...priceData, price: val})}
                    />
                </Box>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose} color="secondary">Cancel</Button>
                <Button onClick={handleSavePrice} variant="contained" startIcon={<EditIcon/>}>
                    Update
                </Button>
            </DialogActions>

        </Dialog>
    );
}