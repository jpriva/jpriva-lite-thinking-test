import axiosClient from '../api/axiosClient';
import type {Company, CreateCompanyRequest} from '../types';

export const CompanyService = {

    getAll: async () => {
        const { data } = await axiosClient.get<Company[]>('/api/companies');
        return data;
    },

    getByTaxId: async (taxId: string) => {
        const { data } = await axiosClient.get<Company>(`/api/companies/${taxId}`);
        return data;
    },

    create: async (payload: CreateCompanyRequest) => {
        const { data } = await axiosClient.post<Company>('/api/companies', payload);
        return data;
    },
};