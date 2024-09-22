import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authToken = localStorage.getItem('token');
  if (authToken) {
    const authReq = req.clone({
      headers: req.headers.set('Authorization', authToken),
    });
    return next(authReq);
  }
  return next(req);
};
