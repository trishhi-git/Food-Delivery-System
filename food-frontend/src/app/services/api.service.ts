import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  // 🔐 Common method to get headers
  private getAuthHeaders() {
    const token = localStorage.getItem('token');

    return {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + token
      })
    };
  }

  // =========================
  // 👤 AUTH APIs
  // =========================

  // ✅ Register
  register(data: any) {
    return this.http.post(`${this.baseUrl}/users`, data);
  }

  // ✅ Login (JWT as plain text)
  login(data: any) {
    return this.http.post(`${this.baseUrl}/auth/login`, data, {
      responseType: 'text'
    });
  }

  // =========================
  // 🍽️ RESTAURANT APIs
  // =========================

  // ✅ Get all restaurants
  getRestaurants() {
  return this.http.get<any[]>(`${this.baseUrl}/restaurants`, this.getAuthHeaders());
}

  // ✅ Add restaurant (OWNER)
  addRestaurant(data: any) {
    return this.http.post(`${this.baseUrl}/restaurants`, data, this.getAuthHeaders());
  }

  // ✅ Add menu item (OWNER)
  addMenuItem(data: any) {
    return this.http.post(`${this.baseUrl}/menu`, data, this.getAuthHeaders());
  }

  // ✅ Get menu by restaurant
  getMenuByRestaurant(restaurantId: number) {
    return this.http.get<any[]>(`${this.baseUrl}/menu/${restaurantId}`, this.getAuthHeaders());
  }

  // =========================
  // 📦 ORDER APIs
  // =========================

  // ✅ Place order (USER)
  placeOrder(order: any) {
    return this.http.post(`${this.baseUrl}/orders`, order, this.getAuthHeaders());
  }

  // ✅ Get all orders
  getOrders() {
  return this.http.get<any[]>(`${this.baseUrl}/orders`, this.getAuthHeaders());
}

  // ✅ Get orders by restaurant (OWNER)
  getOrdersByRestaurant(restaurantId: number) {
    return this.http.get(
      `${this.baseUrl}/orders?restaurantId=${restaurantId}`,
      this.getAuthHeaders()
    );
  }

  // ✅ Update order status
  updateOrder(id: number, data: any) {
    return this.http.put(
      `${this.baseUrl}/orders/${id}`,
      data,
      this.getAuthHeaders()
    );
  }
}