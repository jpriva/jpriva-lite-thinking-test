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