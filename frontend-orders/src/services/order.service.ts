import axiosClient from '../api/axiosClient';
import type {Page, Order, CreateOrderRequest} from '../types';

export const OrderService = {

    getAll: async (taxId: string, page: number, size: number) => {
        const { data } = await axiosClient.get<Page<Order>>(`/api/orders/${taxId}?page=${page}&size=${size}`);
        return data;
    },

    getById: async (id: string) => {
        const { data } = await axiosClient.get<Order>(`/api/orders/${id}`);
        return data;
    },

    create: async (payload: CreateOrderRequest) => {
        const { data } = await axiosClient.post<Order>('/api/orders/admin', payload);
        return data;
    },
};