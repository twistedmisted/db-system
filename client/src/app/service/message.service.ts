import { inject, Injectable } from '@angular/core';
import { ToastService } from './toast.service';

@Injectable({
  providedIn: 'root',
})
export class MessageService {
  toastService: ToastService = inject(ToastService);

  openError(msg: string[]) {
    this.toastService.show({
      nameTpl: 'danger',
      message: msg[0],
      classname: 'bg-danger text-light',
      delay: 15000,
    });
  }

  openSuccess(msg: string) {
    this.toastService.show({
      nameTpl: 'success',
      message: msg,
      classname: 'bg-success text-light',
    });
  }
}
