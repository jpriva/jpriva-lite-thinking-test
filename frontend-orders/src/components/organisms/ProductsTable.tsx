import {DataGrid, type GridColDef} from "@mui/x-data-grid";
import {PriceCell, StockCell} from "../molecules";
import {Box} from "@mui/material";
import type {Currency, Product} from "../../types";
import {useMemo} from "react";

interface Props {
    products: Product[];
    selectedCurrency: Currency;
    isAdmin: boolean;
    isLoading: boolean;
    handleOpenPrice: (product: Product) => void;
    handleOpenStock: (product: Product) => void;
}

export const ProductsTable = ({
    products,
    selectedCurrency,
    isAdmin,
    isLoading,
    handleOpenPrice,
    handleOpenStock
}: Props) => {

    const columns: GridColDef[] = useMemo(() => [
        {field: 'sku', headerName: 'SKU', width: 120, minWidth: 100},
        {field: 'name', headerName: 'Product Name', flex: 1.5, minWidth: 150},
        {
            field: 'stockQuantity',
            headerName: 'Stock',
            width: 130,
            renderCell: (params) => (
                <StockCell
                    value={params.value}
                    showAction={isAdmin}
                    onAddStock={() => handleOpenStock(params.row)}
                />
            )
        },
        {
            field: 'prices',
            headerName: 'Price',
            flex: 1,
            minWidth: 150,
            renderCell: (params) => (
                <PriceCell
                    prices={params.value}
                    currency={selectedCurrency}
                    showAction={isAdmin}
                    onUpdatePrice={() => handleOpenPrice(params.row)}
                />
            )
        },
        {field: 'description', headerName: 'Description', flex: 1, minWidth: 150},
    ], [isAdmin, handleOpenStock, selectedCurrency, handleOpenPrice]);

    return (
        <Box sx={{height: 400, width: '100%'}}>
            <DataGrid rows={products}
                      columns={columns}
                      loading={isLoading}
                      disableRowSelectionOnClick
                      localeText={{
                          noRowsLabel: isLoading ? 'Loading products...' : 'No products found',
                      }}
            />
        </Box>
    );
};