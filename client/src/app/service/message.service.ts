import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class MessageService {
  error$ = new Subject<string[]>();
  success$ = new Subject<string>();

  openError(msg: string[]) {
    this.error$.next(msg);
  }

  openSuccess(msg: string) {
    this.success$.next(msg);
  }

  getError(): Subject<string[]> {
    return this.error$;
  }

  getSuccess(): Subject<string> {
    return this.success$;
  }
}
