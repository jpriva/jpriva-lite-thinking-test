import {Box, Button, Dialog, DialogActions, DialogContent, DialogTitle} from "@mui/material";
import {useState} from "react";
import type {Category, CreateProductRequest} from "../../types";
import {ProductService} from "../../services";
import {FormInput} from "../atoms";
import {CategorySelector} from "../molecules";

interface Props {
    open: boolean;
    onClose: () => void;
    companyId: string | undefined;
    categories: Category[];
    fetchProducts: () => Promise<void>;
}

export const CreateProductDialog = ({ open, onClose, companyId, categories, fetchProducts }: Props) => {

    const [newProduct, setNewProduct] = useState<CreateProductRequest>({
        companyId: companyId || '',
        categoryId: '',
        name: '',
        sku: '',
        description: ''
    });
    const handleCreate = async () => {
        if (!newProduct.name || !newProduct.sku || !newProduct.categoryId) {
            alert("Name, SKU and Category ID are required");
            return;
        }
        try {
            await ProductService.create(newProduct);
            onClose();
            setNewProduct({...newProduct, name: '', sku: '', description: ''});
            void fetchProducts();
        } catch (error) {
            console.error("Error creating product", error);
            alert("Error creating product");
        }
    };
    return (
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>Register New Product</DialogTitle>
            <DialogContent>
                <Box sx={{mt: 1, display: 'flex', flexDirection: 'column', gap: 2, minWidth: 400}}>
                    <FormInput
                        label="Name"
                        value={newProduct.name}
                        onChange={(e) => setNewProduct({...newProduct, name: e.target.value})}
                    />
                    <FormInput
                        label="SKU (Unique Code)"
                        value={newProduct.sku}
                        onChange={(e) => setNewProduct({...newProduct, sku: e.target.value})}
                    />
                    <CategorySelector
                        select
                        label="Category"
                        categories={categories}
                        value={newProduct.categoryId}
                        onChange={(e) => setNewProduct({...newProduct, categoryId: e.target.value})}
                        helperText="Select the category for this product"
                    >
                    </CategorySelector>
                    <FormInput
                        label="Description"
                        multiline
                        rows={3}
                        value={newProduct.description}
                        onChange={(e) => setNewProduct({...newProduct, description: e.target.value})}
                    />
                </Box>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose} color="secondary">Cancel</Button>
                <Button onClick={handleCreate} variant="contained">Save</Button>
            </DialogActions>
        </Dialog>
    );
}