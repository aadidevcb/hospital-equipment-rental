import axios from 'axios';
import { Category, Equipment, Customer, Rental, RentalRequest } from '../types';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Category API
export const categoryAPI = {
  getAll: () => api.get<Category[]>('/categories'),
  getById: (id: number) => api.get<Category>(`/categories/${id}`),
  getWithEquipment: (id: number) => api.get<Category>(`/categories/${id}/with-equipment`),
  create: (category: Omit<Category, 'id'>) => api.post<Category>('/categories', category),
  update: (id: number, category: Omit<Category, 'id'>) => 
    api.put<Category>(`/categories/${id}`, category),
  delete: (id: number) => api.delete(`/categories/${id}`),
};

// Equipment API
export const equipmentAPI = {
  getAll: () => api.get<Equipment[]>('/equipment'),
  getAvailable: () => api.get<Equipment[]>('/equipment/available'),
  getById: (id: number) => api.get<Equipment>(`/equipment/${id}`),
  getWithCategory: (id: number) => api.get<Equipment>(`/equipment/${id}/with-category`),
  getByCategory: (categoryId: number) => 
    api.get<Equipment[]>(`/equipment/category/${categoryId}`),
  getAvailableByCategory: (categoryId: number) => 
    api.get<Equipment[]>(`/equipment/category/${categoryId}/available`),
  search: (keyword: string) => 
    api.get<Equipment[]>(`/equipment/search?keyword=${encodeURIComponent(keyword)}`),
  getByPriceRange: (minPrice: number, maxPrice: number) => 
    api.get<Equipment[]>(`/equipment/price-range?minPrice=${minPrice}&maxPrice=${maxPrice}`),
  getByStatus: (status: string) => 
    api.get<Equipment[]>(`/equipment/status/${status}`),
  create: (equipment: Omit<Equipment, 'id'>) => 
    api.post<Equipment>('/equipment', equipment),
  update: (id: number, equipment: Omit<Equipment, 'id'>) => 
    api.put<Equipment>(`/equipment/${id}`, equipment),
  delete: (id: number) => api.delete(`/equipment/${id}`),
  checkAvailability: (id: number, quantity: number) => 
    api.get<boolean>(`/equipment/${id}/availability?quantity=${quantity}`),
  uploadImage: (id: number, file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post(`/equipment/${id}/image`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
  },
};

// Customer API
export const customerAPI = {
  getAll: () => api.get<Customer[]>('/customers'),
  getById: (id: number) => api.get<Customer>(`/customers/${id}`),
  getWithRentals: (id: number) => api.get<Customer>(`/customers/${id}/with-rentals`),
  getByEmail: (email: string) => api.get<Customer>(`/customers/email/${email}`),
  search: (name: string) => 
    api.get<Customer[]>(`/customers/search?name=${encodeURIComponent(name)}`),
  create: (customer: Omit<Customer, 'id'>) => 
    api.post<Customer>('/customers', customer),
  update: (id: number, customer: Omit<Customer, 'id'>) => 
    api.put<Customer>(`/customers/${id}`, customer),
  delete: (id: number) => api.delete(`/customers/${id}`),
};

// Rental API
export const rentalAPI = {
  getAll: () => api.get<Rental[]>('/rentals'),
  getById: (id: number) => api.get<Rental>(`/rentals/${id}`),
  getWithDetails: (id: number) => api.get<Rental>(`/rentals/${id}/details`),
  getByCustomer: (customerId: number) => 
    api.get<Rental[]>(`/rentals/customer/${customerId}`),
  getByEquipment: (equipmentId: number) => 
    api.get<Rental[]>(`/rentals/equipment/${equipmentId}`),
  getByStatus: (status: string) => 
    api.get<Rental[]>(`/rentals/status/${status}`),
  getOverdue: () => api.get<Rental[]>('/rentals/overdue'),
  getActiveOnDate: (date: string) => 
    api.get<Rental[]>(`/rentals/active?date=${date}`),
  create: (rental: RentalRequest) => api.post<Rental>('/rentals', rental),
  update: (id: number, rental: Partial<Rental>) => 
    api.put<Rental>(`/rentals/${id}`, rental),
  updateStatus: (id: number, status: string) => 
    api.patch<Rental>(`/rentals/${id}/status?status=${status}`),
  delete: (id: number) => api.delete(`/rentals/${id}`),
  checkEquipmentAvailability: (
    equipmentId: number, 
    startDate: string, 
    endDate: string, 
    quantity: number
  ) => 
    api.get<boolean>(
      `/rentals/equipment/${equipmentId}/availability?startDate=${startDate}&endDate=${endDate}&quantity=${quantity}`
    ),
  getAvailableQuantityForPeriod: (
    equipmentId: number, 
    startDate: string, 
    endDate: string
  ) => 
    api.get<number>(
      `/rentals/equipment/${equipmentId}/available-quantity?startDate=${startDate}&endDate=${endDate}`
    ),
  calculateCost: (
    equipmentId: number, 
    startDate: string, 
    endDate: string, 
    quantity: number
  ) => 
    api.get<number>(
      `/rentals/equipment/${equipmentId}/cost?startDate=${startDate}&endDate=${endDate}&quantity=${quantity}`
    ),
};

export default api;