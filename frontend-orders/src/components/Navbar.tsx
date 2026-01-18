import { AppBar, Toolbar, Typography, Button, Box } from '@mui/material';
import { useNavigate, useLocation, matchPath } from 'react-router-dom';
import InventoryIcon from '@mui/icons-material/Inventory';
import ShoppingBagIcon from '@mui/icons-material/ShoppingBag';
import CategoryIcon from '@mui/icons-material/Category';
import GroupsIcon from '@mui/icons-material/Groups';

export const Navbar = () => {
    const navigate = useNavigate();
    const location = useLocation();

    const match = matchPath(
        { path: "/:section/:companyId/*" },
        location.pathname
    );

    const companyId = match?.params.companyId;

    const handleLogout = () => {
        localStorage.clear();
        navigate('/login');
    };

    return (
        <Box sx={{ flexGrow: 1, mb: 3 }}>
            <AppBar position="static">
                <Toolbar>
                    <Typography
                        variant="h6"
                        component="div"
                        sx={{ flexGrow: 1, cursor: 'pointer' }}
                        onClick={() => navigate('/companies')}
                    >
                        {companyId ? "⬅ Return to companies" : "ORDERS"}
                    </Typography>

                    {companyId && (
                        <Box sx={{ mr: 2, display: 'flex', gap: 1 }}>
                            <Button
                                color="inherit"
                                startIcon={<ShoppingBagIcon />}
                                onClick={() => navigate(`/orders/${companyId}`)}
                                sx={{ borderBottom: location.pathname.includes('/orders') ? '2px solid white' : 'none' }}
                            >
                                Orders
                            </Button>

                            <Button
                                color="inherit"
                                startIcon={<InventoryIcon />}
                                onClick={() => navigate(`/products/${companyId}`)}
                                sx={{ borderBottom: location.pathname.includes('/products') ? '2px solid white' : 'none' }}
                            >
                                Products
                            </Button>

                            <Button
                                color="inherit"
                                startIcon={<CategoryIcon />}
                                onClick={() => navigate(`/categories/${companyId}`)}
                                sx={{ borderBottom: location.pathname.includes('/categories') ? '2px solid white' : 'none' }}
                            >
                                Categories
                            </Button>

                            <Button
                                color="inherit"
                                startIcon={<GroupsIcon />}
                                onClick={() => navigate(`/clients/${companyId}`)}
                                sx={{ borderBottom: location.pathname.includes('/clients') ? '2px solid white' : 'none' }}
                            >
                                Clients
                            </Button>
                        </Box>
                    )}

                    <Button color="error" variant="contained" size="small" onClick={handleLogout}>
                        Logout
                    </Button>
                </Toolbar>
            </AppBar>
        </Box>
    );
};