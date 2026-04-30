import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.css']
})
export class UserDashboardComponent {

  restaurants: any[] = [];
  orders: any[] = []; // ✅ FIXED
  
  selectedRestaurant: any = null;
  menuItems: any[] = [];
  selectedItems: Map<number, any> = new Map(); // stores itemId -> item with quantity
  cartTotal: number = 0;

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.getRestaurants().subscribe((res: any) => {
      this.restaurants = res;
    });

    this.loadMyOrders(); // ✅
  }

  selectRestaurant(r: any) {
    this.selectedRestaurant = r;
    this.selectedItems.clear();
    this.cartTotal = 0;
    
    this.api.getMenuByRestaurant(r.id).subscribe((res: any) => {
      this.menuItems = res.map((item: any) => ({ ...item, quantity: 0 }));
    });
  }

  closeMenu() {
    this.selectedRestaurant = null;
    this.menuItems = [];
    this.selectedItems.clear();
    this.cartTotal = 0;
  }

  increaseQuantity(item: any) {
    item.quantity++;
    this.selectedItems.set(item.id, item);
    this.calculateTotal();
  }

  decreaseQuantity(item: any) {
    if (item.quantity > 0) {
      item.quantity--;
      if (item.quantity === 0) {
        this.selectedItems.delete(item.id);
      } else {
        this.selectedItems.set(item.id, item);
      }
      this.calculateTotal();
    }
  }

  calculateTotal() {
    this.cartTotal = 0;
    this.selectedItems.forEach(item => {
      this.cartTotal += item.price * item.quantity;
    });
  }

  placeOrder() {
    if (this.cartTotal === 0 || !this.selectedRestaurant) return;

    // generate a comma-separated string of items: "2x Pizza, 1x Burger"
    const itemsList: string[] = [];
    this.selectedItems.forEach(item => {
      itemsList.push(`${item.quantity}x ${item.name}`);
    });
    const itemsString = itemsList.join(', ');

    const order = {
      userId: localStorage.getItem('userId'),
      restaurantId: this.selectedRestaurant.id,
      totalAmount: this.cartTotal,
      items: itemsString
    };

    this.api.placeOrder(order).subscribe(() => {
      alert('Order placed successfully!');
      this.closeMenu();
      this.loadMyOrders(); // 🔥 refresh orders after placing
    });
  }

  loadMyOrders() {
    const userId = localStorage.getItem('userId');

    this.api.getOrders().subscribe((res: any[]) => {
      this.orders = res.filter(o => o.userId == userId);
    });
  }
}