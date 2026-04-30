import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { OrdersComponent } from './orders/orders.component';
import { UserDashboardComponent } from './dashboard/user-dashboard/user-dashboard.component';
import { OwnerDashboardComponent } from './dashboard/owner-dashboard/owner-dashboard.component';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'orders', component: OrdersComponent } ,
  { path: 'user-dashboard', component: UserDashboardComponent },
  { path: 'owner-dashboard', component: OwnerDashboardComponent }
];