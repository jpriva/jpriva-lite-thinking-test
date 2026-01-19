export interface Product {
    id: string;
    companyId: string;
    categoryId: string;
    name: string;
    sku: string;
    description?: string;
    stockQuantity: number;
    prices: Record<string, number>;
    createdAt: string;
}

export interface CreateProductRequest {
    companyId: string;
    categoryId: string;
    name: string;
    sku: string;
    description?: string;
}

export interface UpdateProductPriceRequest {
    price: number;
    currencyCode: string;
}