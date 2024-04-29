import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);

  // Here you can check for user authentication
  // and redirect to login page if not authenticated
  const localDataToken = localStorage.getItem('token');
  const localDataRefreshToken = localStorage.getItem('refreshToken');
  if (!localDataToken || !localDataRefreshToken) {
    router.navigate(['/login']);
    return false;
  }
  return true;
};
