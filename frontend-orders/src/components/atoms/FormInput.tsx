import { TextField, type TextFieldProps } from '@mui/material';

export const FormInput = (props: TextFieldProps) => (
    <TextField fullWidth variant="outlined" margin="normal" {...props} />
);