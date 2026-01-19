import { MenuItem, TextField } from '@mui/material';

interface Props {
    value: string;
    onChange: (role: string) => void;
}

export const UserRoleSelector = ({ value, onChange }: Props) => (
    <TextField
        select
        fullWidth
        label="Assign Role"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        margin="normal"
    >
        <MenuItem value="ADMIN">Administrator</MenuItem>
        <MenuItem value="EXTERNAL">External</MenuItem>
    </TextField>
);