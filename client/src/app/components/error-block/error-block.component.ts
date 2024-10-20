import { Component, OnDestroy, OnInit } from '@angular/core';
import { ErrorService } from '../../service/error.service';
import { Subject, takeUntil } from 'rxjs';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-error-block',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './error-block.component.html',
  styleUrl: './error-block.component.scss',
})
export class ErrorBlockComponent implements OnInit, OnDestroy {
  constructor(private errorSerivce: ErrorService) {}

  messages: string[] | null = null;
  errorClasses = {
    alert: true,
    show: false,
    success: false,
  };
  destroy$: Subject<boolean> = new Subject<boolean>();

  showError(msg: string[]) {
    this.errorClasses = {
      ...this.errorClasses,
      show: true,
    };
    this.messages = msg;
  }

  closeAlert() {
    this.errorClasses = {
      ...this.errorClasses,
      show: false,
    };
    this.messages = null;
  }

  ngOnInit(): void {
    this.errorSerivce
      .getError()
      .pipe(takeUntil(this.destroy$))
      .subscribe((msg) => {
        this.errorClasses = { ...this.errorClasses, success: false };
        this.showError(msg);
      });
  }

  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }
}
