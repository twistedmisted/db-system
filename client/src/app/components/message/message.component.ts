import { Component, OnDestroy, OnInit } from '@angular/core';
import { MessageService } from '../../service/message.service';
import {} from '@fortawesome/free-solid-svg-icons';
import { Subject, takeUntil } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { console } from 'inspector';

@Component({
  selector: 'app-message',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule],
  templateUrl: './message.component.html',
  styleUrl: './message.component.scss',
})
export class MessageComponent implements OnInit, OnDestroy {
  constructor(private messageService: MessageService) {}

  messages: string[] | null = null;
  alertClasses = {
    alert: true,
    show: false,
    hide: true,
    showAlert: false,
    success: false,
  };
  destroy$: Subject<boolean> = new Subject<boolean>();
  timeout!: NodeJS.Timeout;

  openAlert(msg: string[]) {
    this.alertClasses = {
      ...this.alertClasses,
      hide: false,
      show: true,
      showAlert: true,
    };
    this.messages = msg;
    clearTimeout(this.timeout);
    this.timeout = setTimeout(this.closeAlert.bind(this), 5000);
  }

  closeAlert() {
    this.alertClasses = {
      ...this.alertClasses,
      hide: true,
      show: false,
    };
  }

  ngOnInit(): void {
    this.messageService
      .getError()
      .pipe(takeUntil(this.destroy$))
      .subscribe((msg) => {
        this.alertClasses = { ...this.alertClasses, success: false };
        this.openAlert(msg);
      });
    this.messageService
      .getSuccess()
      .pipe(takeUntil(this.destroy$))
      .subscribe((msg) => {
        this.alertClasses = { ...this.alertClasses, success: true };
        this.openAlert([msg]);
      });
  }

  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }
}
