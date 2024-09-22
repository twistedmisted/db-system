import {
  HttpErrorResponse,
  type HttpInterceptorFn,
} from '@angular/common/http';
import { MessageService } from '../service/message.service';
import { inject } from '@angular/core';
import { catchError, EMPTY } from 'rxjs';
import { ErrorService } from '../service/error.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const messageService: MessageService = inject(MessageService);
  const errorService: ErrorService = inject(ErrorService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.error && !error.error.notification) {
        errorService.openError([error.error.error]);
        // return throwError(() => error);
      } else {
        if (error.status === 0) {
          messageService.openError(['Status code 0']);
        } else {
          messageService.openError([error.error.error]);
        }
      }
      return EMPTY;
    })
  );
};
