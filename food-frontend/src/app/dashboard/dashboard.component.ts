import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../services/api.service';
import { RouterModule } from '@angular/router'; 

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {

  restaurants: any[] = [];
  message = '';

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadRestaurants();
  }

  loadRestaurants() {
    this.api.getRestaurants().subscribe({
      next: (res: any) => {
        this.restaurants = res;
      },
      error: () => {
        this.message = "Failed to load restaurants";
      }
    });
  }

  // ✅ MAIN ORDER METHOD
  placeOrder(restaurant: any) {

    if (!restaurant || !restaurant.id) {
      this.message = "Invalid restaurant selected";
      return;
    }

    const userId = this.getUserIdFromToken();

    const orderData = {
      userId: userId,
      restaurantId: restaurant.id,
      totalAmount: this.calculateAmount(restaurant),
      status: "PLACED"
    };

    this.api.placeOrder(orderData).subscribe({
      next: () => {
        this.message = "Order placed successfully!";
      },
      error: (err) => {
        console.error(err);
        this.message = "Order failed!";
      }
    });
  }

  // ✅ EXTRACT USER (SIMULATED CLEAN WAY)
  getUserIdFromToken(): number {
    const token = localStorage.getItem('token');

    if (!token) return 0;

    const payload = JSON.parse(atob(token.split('.')[1]));

    // 👉 ideally backend should give userId
    // for now we simulate consistent mapping
    return 1;
  }

  // ✅ AMOUNT CALCULATION (EXTENDABLE)
  calculateAmount(restaurant: any): number {
    return 250; // can later depend on menu
  }
}