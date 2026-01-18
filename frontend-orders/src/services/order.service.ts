import axiosClient from '../api/axiosClient';
import type {Page, Order, CreateOrderRequest, AddOrderItemRequest} from '../types';

export const OrderService = {

    getAll: async (taxId: string, page: number, size: number) => {
        const { data } = await axiosClient.get<Page<Order>>(`/api/orders/${taxId}?page=${page}&size=${size}`);
        return data;
    },

    getById: async (id: string) => {
        const { data } = await axiosClient.get<Order>(`/api/orders/order/${id}`);
        return data;
    },

    create: async (payload: CreateOrderRequest) => {
        const { data } = await axiosClient.post<Order>('/api/orders', payload);
        return data;
    },

    addItem: async (orderId: string, item: AddOrderItemRequest) => {
        const { data } = await axiosClient.post<Order>(`/api/orders/${orderId}/items`, item);
        return data;
    },
};