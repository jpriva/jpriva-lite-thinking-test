import {CURRENCIES} from "../../types";
import {Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, MenuItem, TextField} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import {useState} from "react";
import {ProductService} from "../../services";

type UpdateProductPriceDialogProps = {
    open: boolean;
    onClose: () => void;
    companyId: string | undefined;
    selectedProductId: string;
    fetchProducts: () => Promise<void>;
}

export const UpdateProductPriceDialog = ({open, onClose, selectedProductId, fetchProducts}:UpdateProductPriceDialogProps) => {

    const [priceData, setPriceData] = useState({
        price: '',
        currencyCode: 'COP'
    });

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
                <Button onClick={onClose} color="secondary">Cancel</Button>
                <Button onClick={handleSavePrice} variant="contained" startIcon={<EditIcon/>}>
                    Update
                </Button>
            </DialogActions>

        </Dialog>
    );
}