import axiosClient from '../api/axiosClient';
import type {CreateProductRequest, Product, UpdateProductPriceRequest} from '../types';

export const ProductService = {
    getAll: async (taxId: string) => {
        const {data} = await axiosClient.get<Product[]>(`/api/products/${taxId}`);
        return data;
    },

    create: async (payload: CreateProductRequest) => {
        const {data} = await axiosClient.post<Product>('/api/products', payload);
        return data;
    },

    updatePrice: async (productId: string, payload: UpdateProductPriceRequest) => {
        const {data} = await axiosClient.put<Product>(`/api/products/${productId}/price`, payload);
        return data;
    },

    increaseStock: async (id: string, amount: number) => {
        const {data} = await axiosClient.put(`/api/products/${id}/stock`, null, {
            params: {amount}
        });
        return data;
    },

    downloadPdf: async (taxId: string): Promise<Blob> => {
        const {data} = await axiosClient.get<Blob>(`/api/products/${taxId}/pdf`, {
            responseType: 'blob'
        });
        return data;
    },

    emailPdf: async (taxId: string, email: string): Promise<void> => {
        await axiosClient.get<void>(`/api/products/${taxId}/email`, {
            params: { email }
        });
    },
};