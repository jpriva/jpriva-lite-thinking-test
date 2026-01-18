
export interface Category {
  id: string;
  companyId: string;
  name: string;
  description?: string;
}

export interface CreateCategoryRequest {
  companyId: string;
  name: string;
  description?: string;
}


export interface Client {
  id: string;
  companyId: string;
  name: string;
  email?: string;
  phone?: string;
  address?: string;
  createdAt: string;
}

export interface CreateClientRequest {
  companyId: string;
  name: string;
  email?: string;
  phone?: string;
  address?: string;
}


export interface Company {
  id: string;
  name: string;
  taxId: string;
  address?: string;
  phone?: string;
  createdAt: string;
}

export interface CreateCompanyRequest {
  name: string;
  taxId: string;
  address?: string;
  phone?: string;
}


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


export interface User {
  id: string;
  email: string;
  fullName: string;
  phone?: string;
  address?: string;
  role: 'ADMIN' | 'EXTERNAL';
  createdAt: string;
}

export interface CreateUserRequest {
  email: string;
  password: string;
  fullName: string;
  phone?: string;
  address?: string;
  role: 'ADMIN' | 'EXTERNAL';
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface TokenResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  issuedAt: number;
}

export interface ProblemDetails {
  type: string;
  title: string;
  status: number;
  detail?: string;
    instance?: string;
    [key: string]: any;
  }
  
  
  /**
   * Represents a paginated response from the backend, mirroring Spring Boot's Page object.
   * @template T The type of the content items in the page.
   */
  export interface Page<T> {
    content: T[];
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
    first: boolean;
    last: boolean;
    numberOfElements: number;
    empty: boolean;
  }
  