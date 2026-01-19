export const CURRENCIES = [
    {code: 'COP', name: 'Colombian Peso', symbol: '$'},
    {code: 'USD', name: 'US Dollar', symbol: '$'},
    {code: 'EUR', name: 'Euro', symbol: '€'},
    {code: 'GBP', name: 'British Pound', symbol: '£'},
    {code: 'JPY', name: 'Japanese Yen', symbol: '¥'}
];

export interface Page<T> {
    content: T[];
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
    first: boolean;
    last: boolean;
    numberOfElements: number;
    empty: boolean;
}

export type Pageable = {
    page: number;
    size: number;
    sort?: string;
};

export interface ProblemDetails {
    type: string;
    title: string;
    status: number;
    detail?: string;
    instance?: string;

    [key: string]: unknown;
}