import axiosClient from '../api/axiosClient';
import type {Category, CreateCategoryRequest} from '../types';

export const CategoryService = {
    getAll: async (companyId: string) => {
        const {data} = await axiosClient.get<Category[]>(`/api/categories/${companyId}`);
        return data;
    },

    create: async (payload: CreateCategoryRequest) => {
        const {data} = await axiosClient.post<Category>('/api/categories', payload);
        return data;
    },
};