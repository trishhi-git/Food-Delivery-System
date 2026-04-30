import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../services/api.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  user = {
    name: '',
    email: '',
    password: '',
    role: 'USER'
  };

  restaurantName: string = '';
  restaurantLocation: string = '';

  message: string = '';
  validationErrors: any = {};

  constructor(private api: ApiService, private router: Router) {}

  register() {

    if (!this.user.name || !this.user.email || !this.user.password) {
      this.message = 'All fields are required';
      return;
    }

    if (this.user.role === 'OWNER' && (!this.restaurantName || !this.restaurantLocation)) {
      this.message = 'Restaurant name and location are required for owners';
      return;
    }

    this.api.register(this.user).subscribe({
      next: () => {
        if (this.user.role === 'OWNER') {
          // Auto login to get token and create restaurant
          this.api.login({ email: this.user.email, password: this.user.password }).subscribe({
            next: (token: any) => {
              localStorage.setItem('token', token);
              // Decode token
              let base64Url = token.split('.')[1];
              let base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
              const pad = base64.length % 4;
              if (pad) {
                base64 += new Array(5 - pad).join('=');
              }
              const payload = JSON.parse(window.atob(base64));
              localStorage.setItem('role', payload.role);
              localStorage.setItem('userId', payload.userId);

              // Create restaurant
              this.api.addRestaurant({ name: this.restaurantName, location: this.restaurantLocation }).subscribe({
                next: () => {
                  alert('Registered and Restaurant Created successfully!');
                  this.router.navigate(['/owner-dashboard']);
                },
                error: () => {
                  this.message = 'Registered, but failed to create restaurant. Please contact support.';
                }
              });
            },
            error: () => {
              this.message = 'Registered successfully, but failed to auto-login. Please login manually.';
              this.router.navigate(['/']);
            }
          });
        } else {
          alert('Registered successfully!');
          this.router.navigate(['/']);
        }
      },
      error: (err) => {
        this.message = 'Registration failed';
        if (typeof err.error === 'object') {
          this.validationErrors = err.error;
        } else {
          this.message = err.error || 'Registration failed';
        }
      }
    });
  }
}
