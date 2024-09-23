import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  Form,
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { DbService } from '../../service/db.service';
import { DbTokenLifeTimeService } from '../../service/dbtokenlifetime.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-db',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './create-db.component.html',
  styleUrl: './create-db.component.scss',
})
export class CreateDbComponent implements OnInit {
  form!: FormGroup;
  dbTokenLifeTimes!: string[];

  constructor(
    private dbService: DbService,
    private dbTokenLifeTimeService: DbTokenLifeTimeService,
    private formBuilder: FormBuilder,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initDbTokenLifeTimes();
    this.initForm();
  }

  private initDbTokenLifeTimes() {
    this.dbTokenLifeTimeService.getAll().subscribe((res) => {
      this.dbTokenLifeTimes = res.result;
    });
  }

  private initForm() {
    this.form = this.formBuilder.group({
      dbName: [
        '',
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(256),
        ],
      ],
      tokenLifeTime: ['', [Validators.required]],
    });
  }

  submit() {
    this.dbService.save(this.form.getRawValue()).subscribe((res) => {
      this.router.navigate(['dbs']).then(() => window.location.reload());
    });
  }

  validate(formControl: FormControl): boolean {
    return formControl.invalid && (formControl.dirty || formControl.touched);
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

  get dbName(): FormControl {
    return this.form.get('dbName') as FormControl;
  }

  get tokenLifeTime(): FormControl {
    return this.form.get('tokenLifeTime') as FormControl;
  }
}
