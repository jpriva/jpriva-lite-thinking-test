import { MenuItem, TextField, type TextFieldProps, InputAdornment } from '@mui/material';
import {CURRENCIES} from "../../types";
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';

type CurrencySelectorProps = Omit<TextFieldProps, 'onChange'> & {
    value: string;
    onChange: (currencyCode: string) => void;
};

export const CurrencySelector = ({ value, onChange, ...props }: CurrencySelectorProps) => {
    return (
        <TextField
            select
            label={props.label || "Select Currency"}
            value={value}
            onChange={(e) => onChange(e.target.value)}
            slotProps={{
                input: {
                    startAdornment:
                        <InputAdornment position="start">
                            <AttachMoneyIcon fontSize="small" />
                        </InputAdornment>,
                },
            }}
            {...props}
        >
            {CURRENCIES.map((option) => (
                <MenuItem key={option.code} value={option.code}>
                    <span style={{ fontWeight: 'bold', marginRight: '8px' }}>
                        {option.code}
                    </span>
                    {option.name} ({option.symbol})
                </MenuItem>
            ))}
        </TextField>
    );
};