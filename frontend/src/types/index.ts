export interface Category {
  id: number;
  name: string;
  description: string;
}

export interface Equipment {
  id: number;
  name: string;
  description: string;
  model: string;
  manufacturer: string;
  dailyPrice: number;
  availableQuantity: number;
  totalQuantity: number;
  status: 'AVAILABLE' | 'RENTED' | 'MAINTENANCE' | 'RETIRED';
  imageUrl?: string;
  category: Category;
}

export interface Customer {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  address?: string;
  city?: string;
  state?: string;
  zipCode?: string;
}

export interface Rental {
  id: number;
  customer: Customer;
  equipment: Equipment;
  startDate: string;
  endDate: string;
  actualReturnDate?: string;
  quantity: number;
  dailyRate: number;
  totalAmount: number;
  status: 'PENDING' | 'CONFIRMED' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED' | 'OVERDUE';
  createdAt: string;
  updatedAt: string;
  notes?: string;
}

export interface RentalRequest {
  customerId: number;
  equipmentId: number;
  startDate: string;
  endDate: string;
  quantity: number;
  notes?: string;
}