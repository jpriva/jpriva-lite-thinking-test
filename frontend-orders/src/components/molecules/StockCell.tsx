import { Box, IconButton, Tooltip } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import { StockLabel } from '../atoms';

interface Props {
    value: number;
    onAddStock: () => void;
    showAction: boolean;
}

export const StockCell = ({ value, onAddStock, showAction }: Props) => (
    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, height: '100%',width: '100%'}}>
        <StockLabel value={value} />
        {showAction && (
            <Tooltip title="Add Stock">
                <IconButton size="small" color="primary" onClick={onAddStock}>
                    <AddIcon fontSize="small" />
                </IconButton>
            </Tooltip>
        )}
    </Box>
);