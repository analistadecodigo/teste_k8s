import { CanActivateFn, Router } from '@angular/router';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);
  const publicRoutes = ['/login', '/register'];

  if (publicRoutes.includes(state.url)) {
    return true;
  }

  if (isPlatformBrowser(platformId)) {
    const cookies = document.cookie.split(';');
    let token = null;
    for (const cookie of cookies) {
      const [name, value] = cookie.trim().split('=');
      if (name == 'jwt') {
        token = value;
        break;
      }
    }

    if (token) {
      return true;
    }
  }

  router.navigate(['/login']);
  return false;
};
