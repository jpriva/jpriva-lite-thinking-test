import {Navigate, Route, Routes} from 'react-router-dom';
import {CompanyPage} from "./pages/CompanyPage.tsx";
import {LoginPage} from "./pages/LoginPage.tsx";
import {OrdersPage} from "./pages/OrdersPage.tsx";
import {MainLayout} from "./layouts/MainLayout.tsx";
import {ProductsPage} from "./pages/ProductsPage.tsx";
import {CategoriesPage} from "./pages/CategoriesPage.tsx";
import {ClientsPage} from "./pages/ClientsPage.tsx";
import {OrderManagePage} from "./pages/OrderManagePage.tsx";

function App() {
    const isAuthenticated = !!localStorage.getItem('token');

    return (
        <Routes>
            <Route path="/login" element={isAuthenticated ? <Navigate to="/companies"/> : <LoginPage/>}/>
            <Route element={<MainLayout/>}>
                <Route
                    path="/companies"
                    element={isAuthenticated ? <CompanyPage/> : <Navigate to="/login"/>}
                />
                <Route path="/orders/:companyId" element={isAuthenticated ? <OrdersPage/> : <Navigate to="/login"/>}/>
                <Route path="/orders/:companyId/manage/:orderId" element={isAuthenticated ? <OrderManagePage /> : <Navigate to="/login"/>} />
                <Route path="/products/:companyId" element={isAuthenticated ? <ProductsPage/> : <Navigate to="/login"/>}/>
                <Route path="/categories/:companyId" element={isAuthenticated ? <CategoriesPage/> : <Navigate to="/login"/>}/>
                <Route path="/clients/:companyId" element={isAuthenticated ? <ClientsPage/> : <Navigate to="/login"/>}/>

            </Route>
            <Route path="*" element={<Navigate to={isAuthenticated ? "/companies" : "/login"}/>}/>
        </Routes>
    );
}

export default App;