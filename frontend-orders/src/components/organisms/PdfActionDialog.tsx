import {
    Dialog, DialogTitle, DialogContent, DialogActions,
    Box, Button, Select, MenuItem, InputLabel,
    FormControl, CircularProgress, TextField
} from '@mui/material';
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';
import EmailIcon from '@mui/icons-material/Email';
import {AuthService, ProductService} from '../../services';
import {useState} from "react";

interface Props {
    open: boolean;
    onClose: () => void;
    companyId: string | undefined;
}

export const PdfActionDialog = ({ open, onClose, companyId }: Props) => {
    const userEmail = AuthService.getEmail();
    const [method, setMethod] = useState('1');
    const [loading, setLoading] = useState(false);
    const [email, setEmail] = useState(userEmail);

    const handleAction = async () => {
        if (!companyId) return;

        setLoading(true);
        try {
            if (method === '1') {
                const blob = await ProductService.downloadPdf(companyId);
                const url = window.URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', `inventory-${companyId}.pdf`);
                document.body.appendChild(link);
                link.click();
                link.parentNode?.removeChild(link);
                window.URL.revokeObjectURL(url);
            } else {
                if (!email) {
                    alert("Please enter a valid email");
                    setLoading(false);
                    return;
                }
                await ProductService.emailPdf(companyId, email);
                alert("Email sent successfully!");
            }
            onClose();
        } catch (error) {
            console.error("Action failed:", error);
            alert("An error occurred");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
            <DialogTitle>PDF Export</DialogTitle>
            <DialogContent>
                <Box sx={{ mt: 2, display: 'flex', flexDirection: 'column', gap: 3 }}>
                    <FormControl fullWidth>
                        <InputLabel id="pdf-method-label">Method</InputLabel>
                        <Select
                            labelId="pdf-method-label"
                            value={method}
                            label="Method"
                            onChange={(e) => setMethod(e.target.value)}
                            disabled={loading}
                        >
                            <MenuItem value="1">Download PDF</MenuItem>
                            <MenuItem value="2">Send by Email</MenuItem>
                        </Select>
                    </FormControl>

                    {method === '2' && (
                        <TextField
                            fullWidth
                            label="Recipient Email"
                            type="email"
                            placeholder="example@mail.com"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            disabled={loading}
                            autoFocus
                        />
                    )}
                </Box>
            </DialogContent>
            <DialogActions sx={{ p: 2 }}>
                <Button onClick={onClose} color="secondary" disabled={loading}>
                    Cancel
                </Button>
                <Button
                    onClick={handleAction}
                    variant="contained"
                    disabled={loading || (method === '2' && !email)}
                    startIcon={loading ? <CircularProgress size={20} /> : (method === '1' ? <PictureAsPdfIcon /> : <EmailIcon />)}
                >
                    {method === '1' ? 'Download' : 'Send Email'}
                </Button>
            </DialogActions>
        </Dialog>
    );
};