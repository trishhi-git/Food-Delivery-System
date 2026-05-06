import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';

/**
 * RoleGuard — ensures the logged-in user has the required role for a route.
 * Uses route.data['role'] to determine the required role.
 * Redirects to the correct dashboard if the role doesn't match.
 */
export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const router = inject(Router);
  const userRole = localStorage.getItem('role');
  const requiredRole: string = route.data['role'];

  if (userRole === requiredRole) {
    return true;
  }

  // Redirect to the appropriate dashboard based on actual role
  if (userRole === 'OWNER') {
    router.navigate(['/owner-dashboard']);
  } else if (userRole === 'USER') {
    router.navigate(['/user-dashboard']);
  } else {
    // Not logged in
    router.navigate(['/']);
  }

  return false;
};
