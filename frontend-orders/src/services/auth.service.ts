import axiosClient from "../api/axiosClient.ts";
import type {CreateUserRequest, CustomJwtPayload, LoginRequest, TokenResponse, User} from "../types";
import {jwtDecode} from "jwt-decode";

const TOKEN_KEY = "token";
const ROLE_KEY = "role";
const EMAIL_KEY = "sub";

export const AuthService = {
    login: async (payload: LoginRequest) => {
        const {data} = await axiosClient.post<TokenResponse>(`/auth/login`, payload);
        if (data.accessToken) {
            const decoded = jwtDecode<CustomJwtPayload>(data.accessToken);
            const userRole: string = decoded.roles || decoded.authorities || "EXTERNAL";
            const email: string = decoded.sub || '';
            localStorage.setItem(ROLE_KEY, userRole);
            localStorage.setItem(EMAIL_KEY, email);
            localStorage.setItem(TOKEN_KEY, data.accessToken);
            window.location.href = '/companies';
        }
    },

    register: async (payload: CreateUserRequest) => {
        const {data} = await axiosClient.post<User>('/auth/register', payload);
        return data;
    },

    logout: () => {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.clear();

        window.location.href = '/login';
    },

    getToken: () => {
        return localStorage.getItem(TOKEN_KEY);
    },

    getEmail: () => {
        return localStorage.getItem(EMAIL_KEY);
    },

    isAuthenticated: () => {
        const token = localStorage.getItem(TOKEN_KEY);
        return !!token;
    },

    isAdmin: () => {
        const role = localStorage.getItem(ROLE_KEY);
        return !!role && role === "ADMIN";
    }

};