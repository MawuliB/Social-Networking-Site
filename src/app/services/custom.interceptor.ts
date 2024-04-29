import { HttpInterceptorFn } from '@angular/common/http';

export const customInterceptor: HttpInterceptorFn = (req, next) => {
  debugger;

  const myToken = localStorage.getItem('token');

  const cloned = req.clone({
    headers: req.headers.set('Authorization', `Bearer ${myToken}`)
  });

  return next(cloned);
};
