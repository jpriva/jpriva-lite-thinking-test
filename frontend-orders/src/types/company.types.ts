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