import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-owner-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './owner-dashboard.component.html',
  styleUrls: ['./owner-dashboard.component.css']
})
export class OwnerDashboardComponent {

  restaurant = {
    name: '',
    location: ''
  };

  restaurants: any[] = [];
  orders: any[] = [];
  menuItems: any[] = [];
  selectedRestaurantId: number | null = null;
  newMenuItem = {
    name: '',
    description: '',
    price: null as number | null
  };


  constructor(private api: ApiService) {}

  addRestaurant() {
    this.api.addRestaurant(this.restaurant).subscribe(() => {
      alert('Restaurant added');
      this.restaurant = { name: '', location: '' };
      this.ngOnInit();
    });
  }

  ngOnInit() {
  const ownerId = localStorage.getItem('userId');

  this.api.getRestaurants().subscribe((res: any[]) => {
    // ✅ filter only owner restaurants
    this.restaurants = res.filter(r => r.ownerId == ownerId);
    if (this.restaurants.length > 0) {
      this.loadOrders(this.restaurants[0].id);
    }
  });
}

// 🔥 Load orders and menu for selected restaurant
loadOrders(restaurantId: number) {
  this.selectedRestaurantId = restaurantId;

  this.api.getOrdersByRestaurant(restaurantId).subscribe((res: any) => {
    this.orders = res;
  });
  
  this.loadMenu(restaurantId);
}

loadMenu(restaurantId: number) {
  this.api.getMenuByRestaurant(restaurantId).subscribe((res: any) => {
    this.menuItems = res;
  });
}

addMenuItem() {
  if (this.selectedRestaurantId && this.newMenuItem.name && this.newMenuItem.price) {
    const item = {
      restaurantId: this.selectedRestaurantId,
      name: this.newMenuItem.name,
      description: this.newMenuItem.description,
      price: this.newMenuItem.price
    };
    this.api.addMenuItem(item).subscribe(() => {
      alert('Menu Item added');
      this.newMenuItem.name = '';
      this.newMenuItem.description = '';
      this.newMenuItem.price = null;
      this.loadMenu(this.selectedRestaurantId!);
    });
  }
}

deleteMenuItem(id: number) {
  if (confirm('Are you sure you want to delete this menu item?')) {
    this.api.deleteMenuItem(id).subscribe(() => {
      alert('Menu Item deleted');
      this.loadMenu(this.selectedRestaurantId!);
    });
  }
}

updateStatus(id: number, status: string) {
  this.api.updateOrder(id, { status }).subscribe(() => {
    this.loadOrders(this.selectedRestaurantId!);
  });
}
}
