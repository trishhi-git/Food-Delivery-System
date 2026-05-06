import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { OrdersComponent } from './orders/orders.component';
import { UserDashboardComponent } from './dashboard/user-dashboard/user-dashboard.component';
import { OwnerDashboardComponent } from './dashboard/owner-dashboard/owner-dashboard.component';
import { authGuard } from './guards/auth.guard';
import { roleGuard } from './guards/role.guard';

export const routes: Routes = [
  // ✅ PUBLIC — no guards needed
  { path: '', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  // ✅ PROTECTED — must be logged in
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authGuard]
  },
  {
    path: 'orders',
    component: OrdersComponent,
    canActivate: [authGuard]
  },

  // ✅ ROLE-PROTECTED — USER only
  {
    path: 'user-dashboard',
    component: UserDashboardComponent,
    canActivate: [authGuard, roleGuard],
    data: { role: 'USER' }
  },

  // ✅ ROLE-PROTECTED — OWNER only
  {
    path: 'owner-dashboard',
    component: OwnerDashboardComponent,
    canActivate: [authGuard, roleGuard],
    data: { role: 'OWNER' }
  }
];