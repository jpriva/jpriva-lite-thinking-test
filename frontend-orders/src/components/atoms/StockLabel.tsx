import { Typography } from '@mui/material';

interface Props {
    value: number;
}

export const StockLabel = ({ value }: Props) => (
    <Typography
        fontWeight="bold"
        color={value < 10 ? 'error.main' : 'success.main'}
    >
        {value}
    </Typography>
);