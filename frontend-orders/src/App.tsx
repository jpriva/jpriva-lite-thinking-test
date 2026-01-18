import { Routes, Route, Navigate } from 'react-router-dom';
import {CompanyPage} from "./pages/CompanyPage.tsx";
import {LoginPage} from "./pages/LoginPage.tsx";
import {OrdersPage} from "./pages/OrdersPage.tsx";

function App() {
    const isAuthenticated = !!localStorage.getItem('token');

    return (
        <Routes>
            <Route path="/login" element={isAuthenticated ? <Navigate to="/companies" /> : <LoginPage />} />
            <Route
                path="/companies"
                element={isAuthenticated ? <CompanyPage /> : <Navigate to="/login" />}
            />
            <Route path="/orders/:companyId" element={isAuthenticated ? <OrdersPage /> : <Navigate to="/login" />} />

            <Route path="*" element={<Navigate to={isAuthenticated ? "/companies" : "/login"} />} />
        </Routes>
    );
}

export default App;