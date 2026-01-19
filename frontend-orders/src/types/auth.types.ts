import type {JwtPayload} from "jwt-decode";

export interface User {
    id: string;
    email: string;
    fullName: string;
    phone?: string;
    address?: string;
    role: 'ADMIN' | 'EXTERNAL';
    createdAt: string;
}

export interface CreateUserRequest {
    email: string;
    password: string;
    fullName: string;
    phone?: string;
    address?: string;
    role: 'ADMIN' | 'EXTERNAL';
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface TokenResponse {
    accessToken: string;
    tokenType: string;
    expiresIn: number;
    issuedAt: number;
}

export interface CustomJwtPayload extends JwtPayload {
    roles?: string;
    authorities?: string;
    sub?: string;
    jti?: string;
    iat?: number;
    exp?: number;
    alg?: string;
}