import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { FieldTypeService } from '../../service/fieldtype.service';
import { BehaviorSubject, catchError, EMPTY } from 'rxjs';
import { ConstraintService } from '../../service/constraint.service';
import { Constraint } from '../../models/constraint.model';
import { HttpClient } from '@angular/common/http';
import { BASE_URL } from '../../constants';
import { ConstraintsResponse } from '../../models/response/ConstraintsResponse';
import { TableService } from '../../service/table.service';
import { CreateTableRequest } from '../../models/request/CreateTableRequest';
import { ErrorBlockComponent } from '../error-block/error-block.component';
import { ErrorService } from '../../service/error.service';

@Component({
  selector: 'app-create-table',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    ErrorBlockComponent,
  ],
  templateUrl: './create-table.component.html',
  styleUrl: './create-table.component.scss',
})
export class CreateTableComponent implements OnInit {
  form!: FormGroup;
  fieldTypes$ = new BehaviorSubject<string[]>([]);
  constraints!: Constraint[];
  private dbId = history.state.dbId;

  constructor(
    private tableService: TableService,
    private fieldTypeService: FieldTypeService,
    private constraintService: ConstraintService,
    private router: Router,
    private formBuilder: FormBuilder,
    private errorService: ErrorService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.initFormGroup();

    this.initConstraints();
    this.initFieldTypes();
  }

  private initFormGroup() {
    this.form = this.formBuilder.group({
      dbId: [0, [Validators.required]],
      name: [
        '',
        [
          Validators.required,
          Validators.minLength(1),
          Validators.maxLength(255),
        ],
      ],
      columns: this.formBuilder.array([]),
    });
  }

  private initFieldTypes() {
    this.fieldTypes$ = this.fieldTypeService.getFieldTypes();
  }

  private initConstraints() {
    this.constraintService.getConstraints().subscribe((res) => {
      this.constraints = res.result;
      this.addColumn();
    });
  }

  appendNewItem(item: Constraint): void {
    this.columns.push(
      new FormGroup({
        status: new FormControl(item.value),
        price: new FormControl(item.statusValue),
      })
    );
  }

  get columns(): FormArray {
    return this.form.get('columns') as FormArray;
  }

  addColumn(): void {
    const columnForm = this.formBuilder.group({
      name: [
        '',
        [
          Validators.required,
          Validators.minLength(1),
          Validators.maxLength(255),
        ],
      ],
      columnType: this.formBuilder.group({
        type: ['', [Validators.required]],
        value: null,
      }),
      columnConstraints: this.formBuilder.array(
        Object.keys(this.constraints).map((key) => false)
      ),
    });
    this.columns.push(columnForm);
  }

  removeColumn(index: number): void {
    if (this.columns.length > 1) {
      this.columns.removeAt(index);
    }
  }

  submit(): void {
    const newColumns = this.form.getRawValue().columns.map((c: any) => {
      let t = c.columnConstraints
        .map((v: any, i: number) => v && this.constraints[i])
        .filter((v: any) => !!v);
      return Object.assign({}, c, {
        columnConstraints: t,
      });
    });
    const valueToSave = Object.assign({}, this.form.value, {
      columns: newColumns,
    });

    const createTableRequest: CreateTableRequest = {
      dbId: this.dbId,
      name: valueToSave.name,
      columns: valueToSave.columns,
    };

    this.tableService
      .save(createTableRequest)
      // .pipe(
      //   catchError((err) => {
      //     this.errorService.openError([err.error.error]);
      //     return EMPTY;
      //   })
      // )
      .subscribe((res) => {
        this.router.navigate(['dbs', this.dbId]);
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

  get tableName(): FormControl {
    return this.form.get('name') as FormControl;
  }

  getColumn(index: number) {
    return this.form.get('columns')?.get(`${index}`);
  }

  getColumnName(index: number): FormControl {
    return this.getColumn(index)?.get('name') as FormControl;
  }

  getColumnType(index: number): FormControl {
    return this.getColumn(index)?.get('columnType')?.get('type') as FormControl;
  }
}
