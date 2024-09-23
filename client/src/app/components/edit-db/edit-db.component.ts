import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { DbService } from '../../service/db.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-edit-db',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './edit-db.component.html',
  styleUrl: './edit-db.component.scss',
})
export class EditDbComponent implements OnInit {
  form!: FormGroup;
  curDbName!: string;
  dbId!: string;

  constructor(
    private dbService: DbService,
    private formBuilder: FormBuilder,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.dbId = this.activatedRoute.snapshot.paramMap.get('id')!;
    this.initForm();
    this.initDbName();
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
    });
  }

  private initDbName() {
    this.dbService.getById(this.dbId).subscribe((res) => {
      this.curDbName = res.result.name;
    });
  }

  submit() {
    this.dbService
      .updateName(this.dbId, this.form.getRawValue())
      .subscribe((res) => {
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
}
