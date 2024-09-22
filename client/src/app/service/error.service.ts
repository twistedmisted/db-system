import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ErrorService {
  error$ = new Subject<string[]>();

  openError(msg: string[]) {
    this.error$.next(msg);
  }

  getError(): Subject<string[]> {
    return this.error$;
  }
}
