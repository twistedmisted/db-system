import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { Emitters } from '../../emitters/emitters';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  form: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {
    this.form = this.formBuilder.group({
      username: [
        '',
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(50),
        ],
      ],
      password: ['', [Validators.required, Validators.minLength(8)]],
    });
  }

  submit(): void {
    this.http
      .post('http://localhost:8080/auth/login', this.form.getRawValue())
      .subscribe((res) => {
        const token = (res as any).accessToken;
        localStorage.setItem('token', 'Bearer ' + token);
        Emitters.authEmitter.emit(true);
        this.router.navigate(['/']);
      });
  }

  validate(formControl: FormControl): boolean {
    return formControl.invalid && (formControl.dirty || formControl.touched);
  }

  get username(): FormControl {
    return this.form.get('username') as FormControl;
  }

  get password(): FormControl {
    return this.form.get('password') as FormControl;
  }

  isRequired(formContol: FormControl): boolean {
    return formContol.errors?.['required'];
  }

  invMinLength(formControl: FormControl): boolean {
    return formControl.errors?.['minlength'];
  }

  invMaxLength(formControl: FormControl): boolean {
    return formControl.errors?.['maxlength'];
  }
}
