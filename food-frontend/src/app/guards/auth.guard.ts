import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

/**
 * AuthGuard — ensures the user is logged in (has a JWT token).
 * If no token is found in localStorage, redirects to the login page.
 */
export const authGuard: CanActivateFn = () => {
  const router = inject(Router);
  const token = localStorage.getItem('token');

  if (token) {
    return true;
  }

  // Not logged in — redirect to login
  router.navigate(['/']);
  return false;
};
