import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { IsTokenValidResponse } from '../models/response/IsTokenValidResponse';
import { AuthService } from '../service/auth.service';
import { map } from 'rxjs';
import { MessageService } from '../service/message.service';
import { Emitters } from '../emitters/emitters';

export const authGuard: CanActivateFn = (route, state) => {
  if (localStorage.getItem('token')) {
    return inject(AuthService)
      .validateToken()
      .pipe(
        map((res: IsTokenValidResponse) => {
          if (res.isValid) {
            Emitters.authEmitter.emit(true);
            return true;
          }
          localStorage.removeItem('token');
          inject(MessageService).openError(['Session expired']);
          Emitters.authEmitter.emit(false);
          inject(Router).navigate(['/login']);
          return false;
        })
      );
  }
  Emitters.authEmitter.emit(false);
  inject(Router).navigate(['/login']);
  return false;
};
