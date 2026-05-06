import { Component, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

// Declare Razorpay as a global variable (loaded via CDN in index.html)
declare var Razorpay: any;

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.css']
})
export class UserDashboardComponent {

  restaurants: any[] = [];
  orders: any[] = [];

  selectedRestaurant: any = null;
  menuItems: any[] = [];
  selectedItems: Map<number, any> = new Map();
  cartTotal: number = 0;
  deliveryAddress: string = '';

  paymentMessage: string = '';
  paymentSuccess: boolean = false;
  isProcessingOrder: boolean = false;

  constructor(private api: ApiService, private zone: NgZone) {}

  ngOnInit() {
    this.api.getRestaurants().subscribe((res: any) => {
      this.restaurants = res;
    });

    this.loadMyOrders();
  }

  selectRestaurant(r: any) {
    this.selectedRestaurant = r;
    this.selectedItems.clear();
    this.cartTotal = 0;
    this.paymentMessage = '';

    this.api.getMenuByRestaurant(r.id).subscribe((res: any) => {
      this.menuItems = res.map((item: any) => ({ ...item, quantity: 0 }));
    });
  }

  closeMenu() {
    this.selectedRestaurant = null;
    this.menuItems = [];
    this.selectedItems.clear();
    this.cartTotal = 0;
    this.paymentMessage = '';
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
    if (this.cartTotal === 0 || !this.selectedRestaurant || this.isProcessingOrder) return;

    if (!this.deliveryAddress || this.deliveryAddress.trim() === '') {
      alert("Please enter a valid delivery address.");
      return;
    }

    this.isProcessingOrder = true;

    const itemsList: string[] = [];
    this.selectedItems.forEach(item => {
      itemsList.push(`${item.quantity}x ${item.name}`);
    });
    const itemsString = itemsList.join(', ');

    const order = {
      userId: Number(localStorage.getItem('userId')),
      restaurantId: this.selectedRestaurant.id,
      totalAmount: this.cartTotal,
      items: itemsString,
      deliveryAddress: this.deliveryAddress || 'Pickup'
    };

    this.paymentMessage = 'Creating order...';
    this.paymentSuccess = false;

    // Step 1: Create order on backend (also creates Razorpay order)
    this.api.placeOrder(order).subscribe({
      next: (res: any) => {
        if (res.razorpayOrderId && res.razorpayOrderId !== 'PAYMENT_SERVICE_DOWN') {
          this.isProcessingOrder = false;
          this.openRazorpayModal(res);
        } else {
          // Razorpay service was down — order still placed, skip payment modal
          this.paymentMessage = '✅ Order placed! Payment gateway was unavailable — pay on delivery.';
          this.paymentSuccess = true;
          this.isProcessingOrder = false;
          this.loadMyOrders();
          setTimeout(() => this.closeMenu(), 4000);
        }
      },
      error: (err: any) => {
        this.paymentMessage = '❌ Failed to place order. Please try again.';
        this.isProcessingOrder = false;
        console.error('Order creation error:', err);
      }
    });
  }

  openRazorpayModal(orderData: any) {
    try {
      const options = {
        key: orderData.razorpayKeyId,
        amount: orderData.totalAmount * 100, // Razorpay expects paise
        currency: 'INR',
        name: 'FoodHub',
        description: `Order from ${this.selectedRestaurant?.name}`,
        order_id: orderData.razorpayOrderId,
        handler: (response: any) => {
          this.zone.run(() => {
            // ✅ Payment succeeded — confirm with backend
            this.api.updatePaymentStatus(
              orderData.orderId,
              response.razorpay_order_id,
              'SUCCESS'
            ).subscribe({
              next: () => {
                this.paymentMessage = '✅ Payment successful! Your order is confirmed.';
                this.paymentSuccess = true;
                this.closeMenu();
                this.loadMyOrders();
              },
              error: () => {
                this.paymentMessage = '⚠️ Payment done but confirmation failed. Contact support.';
              }
            });
          });
        },
        prefill: {
          email: localStorage.getItem('userEmail') || '',
        },
        theme: {
          color: '#FF5722'
        },
        modal: {
          ondismiss: () => {
            this.zone.run(() => {
              // User closed the Razorpay modal without paying
              this.api.updatePaymentStatus(
                orderData.orderId,
                'CANCELLED_BY_USER',
                'FAILED'
              ).subscribe(() => {
                this.paymentMessage = '⚠️ Payment was cancelled. Your order is cancelled.';
                this.paymentSuccess = false;
                this.closeMenu();
                this.loadMyOrders();
              });
            });
          }
        }
      };

      if (typeof Razorpay === 'undefined') {
        throw new Error("Razorpay SDK is not loaded. Please disable ad-blockers or check your internet connection.");
      }

      const rzp = new Razorpay(options);
      
      rzp.on('payment.failed', function (response: any) {
         console.error("Payment Failed", response.error);
      });

      rzp.open();
    } catch (error: any) {
      console.error("Error opening Razorpay:", error);
      this.paymentMessage = '⚠️ Failed to open payment gateway. ' + (error.message || 'Check your browser console.');
      this.paymentSuccess = false;
    }
  }

  loadMyOrders() {
    const userId = Number(localStorage.getItem('userId'));
    this.api.getOrders().subscribe((res: any[]) => {
      this.orders = res.filter(o => Number(o.userId) === userId);
      this.orders.reverse(); // Show latest orders first
    });
  }

  cancelOrder(order: any) {
    if(confirm('Are you sure you want to cancel this order?')) {
      this.api.cancelOrder(order.orderId).subscribe({
        next: () => {
          alert('Order Cancelled Successfully');
          this.loadMyOrders();
        },
        error: () => alert('Failed to cancel order')
      });
    }
  }

  rateOrder(order: any, rating: number) {
    this.api.rateOrder(order.orderId, rating).subscribe({
      next: () => {
        alert('Thank you for rating!');
        this.loadMyOrders();
      },
      error: () => alert('Failed to submit rating')
    });
  }
}