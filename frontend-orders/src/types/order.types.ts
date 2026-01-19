export interface OrderItem {
    id: string;
    productId: string;
    productName: string;
    quantity: number;
    unitPrice: number;
}

export interface Order {
    id: string;
    companyId: string;
    clientId: string;
    clientName: string;
    address: string;
    orderDate: string;
    status: string;
    totalAmount: number;
    currency: string;
    items: OrderItem[];
}

export interface CreateOrderRequest {
    companyId: string;
    clientId: string;
    currencyCode: string;
}

export interface AddOrderItemRequest {
    productId: string;
    quantity: number;
}

export interface ChangeOrderItemQuantityRequest {
    productId: string;
    quantity: number;
}