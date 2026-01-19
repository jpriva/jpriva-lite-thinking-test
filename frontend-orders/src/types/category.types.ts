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