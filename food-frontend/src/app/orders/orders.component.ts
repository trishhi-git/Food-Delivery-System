import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../services/api.service';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './orders.component.html'
})
export class OrdersComponent implements OnInit {

  orders: any[] = [];
  message = '';

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.api.getOrders().subscribe({
      next: (res: any) => {
        this.orders = res;
      },
      error: () => {
        this.message = "Failed to load orders";
      }
    });
  }
}
