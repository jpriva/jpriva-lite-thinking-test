import type {Currency} from "../../types";
import {Typography} from "@mui/material";

interface Props {
    amount: number | undefined;
    currency: Currency | undefined;
}

export const PriceLabel = ({amount, currency}: Props) => {
    const formatter = new Intl.NumberFormat('es-CO', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
    });

    const formattedAmount = amount !== undefined ? formatter.format(amount) : '0,00';
    return <Typography variant="body2" component="span">
        <strong style={{marginRight: '8px'}}>
            {currency ? `${currency.code} ${currency.symbol}` : '$'}
        </strong>
        {formattedAmount}
    </Typography>
};