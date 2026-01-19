import axiosClient from '../api/axiosClient';
import type {Client, CreateClientRequest} from '../types';

export const ClientService = {
    getAll: async (taxId: string) => {
        const {data} = await axiosClient.get<Client[]>(`/api/clients/${taxId}`);
        return data;
    },

    create: async (payload: CreateClientRequest) => {
        const {data} = await axiosClient.post<Client>('/api/clients', payload);
        return data;
    },
};