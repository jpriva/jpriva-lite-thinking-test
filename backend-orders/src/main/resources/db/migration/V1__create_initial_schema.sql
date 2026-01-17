CREATE TABLE companies (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    name NVARCHAR(255) NOT NULL,
    tax_id NVARCHAR(50) NOT NULL UNIQUE,
    address NVARCHAR(500),
    phone NVARCHAR(50),
    created_at DATETIME2 DEFAULT GETDATE()
);

CREATE TABLE users (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    email NVARCHAR(255) NOT NULL UNIQUE,
    password_hash NVARCHAR(255) NOT NULL,
    full_name NVARCHAR(255) NOT NULL,
    phone NVARCHAR(50),
    address NVARCHAR(500),
    role NVARCHAR(50) NOT NULL,
    created_at DATETIME2 DEFAULT GETDATE()
);

CREATE TABLE categories (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    company_id UNIQUEIDENTIFIER NOT NULL,
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(500),
    CONSTRAINT FK_Categories_Companies FOREIGN KEY (company_id) REFERENCES companies(id)
);

CREATE TABLE clients (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    company_id UNIQUEIDENTIFIER NOT NULL,
    name NVARCHAR(255) NOT NULL,
    email NVARCHAR(255),
    phone NVARCHAR(50),
    address NVARCHAR(500),
    user_id UNIQUEIDENTIFIER,
    created_at DATETIME2 DEFAULT GETDATE(),
    CONSTRAINT FK_Clients_Companies FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT FK_Clients_Users FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE products (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    company_id UNIQUEIDENTIFIER NOT NULL,
    category_id UNIQUEIDENTIFIER,
    name NVARCHAR(255) NOT NULL,
    sku NVARCHAR(100),
    description NVARCHAR(MAX),
    created_at DATETIME2 DEFAULT GETDATE(),
    CONSTRAINT FK_Products_Companies FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT FK_Products_Categories FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE inventory (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    product_id UNIQUEIDENTIFIER NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    location NVARCHAR(255),
    last_updated DATETIME2 DEFAULT GETDATE(),
    CONSTRAINT FK_Inventory_Products FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT UQ_Inventory_Product_Location UNIQUE (product_id, location)
);

CREATE TABLE product_prices (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    product_id UNIQUEIDENTIFIER NOT NULL,
    currency_code NVARCHAR(3) NOT NULL,
    price DECIMAL(18, 2) NOT NULL,
    CONSTRAINT FK_ProductPrices_Products FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT UQ_ProductPrices_Product_Currency UNIQUE (product_id, currency_code)
);

CREATE TABLE orders (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    company_id UNIQUEIDENTIFIER NOT NULL,
    client_id UNIQUEIDENTIFIER NOT NULL,
    order_date DATETIME2 NOT NULL DEFAULT GETDATE(),
    status NVARCHAR(50) NOT NULL,
    currency_code NVARCHAR(3) NOT NULL,
    total_amount DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    CONSTRAINT FK_Orders_Companies FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT FK_Orders_Clients FOREIGN KEY (client_id) REFERENCES clients(id)
);

CREATE TABLE order_items (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    order_id UNIQUEIDENTIFIER NOT NULL,
    product_id UNIQUEIDENTIFIER NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(18, 2) NOT NULL,
    CONSTRAINT FK_OrderItems_Orders FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT FK_OrderItems_Products FOREIGN KEY (product_id) REFERENCES products(id)
);