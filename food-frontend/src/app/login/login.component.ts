import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../services/api.service';
import { CommonModule } from '@angular/common';

import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  email: string = '';
  password: string = '';
  message: string = '';
  loading: boolean = false;

  selectedRole: string = 'USER';

  constructor(private api: ApiService, private router: Router) {}

  login() {

    // 🔒 Basic validation
    if (!this.email || !this.password) {
      this.message = 'Please enter email and password';
      return;
    }

    this.loading = true;
    this.message = '';

    const data = {
      email: this.email,
      password: this.password
    };

    this.api.login(data).subscribe({

      next: (res: any) => {

        this.loading = false;

        // ✅ Save JWT token
        localStorage.setItem('token', res);

        try {
          // ✅ Decode JWT safely (handle Base64Url encoding)
          let base64Url = res.split('.')[1];
          let base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
          
          // Pad string with '=' to make its length a multiple of 4
          const pad = base64.length % 4;
          if (pad) {
            base64 += new Array(5 - pad).join('=');
          }
          
          const payload = JSON.parse(window.atob(base64));

          const role = payload.role;
          const userId = payload.userId;

          // Optional (useful later)
          localStorage.setItem('role', role);
          localStorage.setItem('userId', userId);

          

          // ✅ Role-based navigation
          if (role === 'OWNER') {
            this.router.navigate(['/owner-dashboard']);
          } else if (role === 'USER') {
            this.router.navigate(['/user-dashboard']);
          } else {
            this.router.navigate(['/login']);
          }

        } catch (e) {
          // ❗ If token decode fails
          this.message = 'Invalid token received';
        }
      },

      error: (err) => {

        this.loading = false;

        // ✅ Clean error handling
        if (err.status === 401) {
          this.message = 'Invalid email or password';
        } else if (err.error && typeof err.error === 'object') {
          this.message = err.error.error || JSON.stringify(err.error);
        } else if (err.error) {
          try {
            const parsed = JSON.parse(err.error);
            this.message = parsed.error || err.error;
          } catch (e) {
            this.message = err.error;
          }
        } else {
          this.message = 'Login failed. Please try again';
        }
      }
    });
  }
}