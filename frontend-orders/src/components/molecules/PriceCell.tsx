import {Box, IconButton} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import {PriceLabel} from '../atoms';
import type {Currency} from "../../types";

interface Props {
    prices: Record<string, number>;
    onUpdatePrice: () => void;
    showAction: boolean;
    currency: Currency;
}

export const PriceCell = ({ prices, onUpdatePrice, showAction, currency }: Props) => {
    const entry = Object.entries(prices).find(([code]) => code === currency.code);
    const amount = entry ? entry[1] : undefined;

    return (
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, height: '100%',width: '100%' }}>
            <PriceLabel
                amount={amount}
                currency={currency}
            />
            {showAction && (
                <IconButton size="small" onClick={onUpdatePrice}>
                    <EditIcon fontSize="small" />
                </IconButton>
            )}
        </Box>
    );
};