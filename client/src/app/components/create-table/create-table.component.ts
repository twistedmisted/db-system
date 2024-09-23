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
import { TableService } from '../../service/table.service';
import { CreateTableRequest } from '../../models/request/CreateTableRequest';
import { ErrorBlockComponent } from '../error-block/error-block.component';
import { Table } from '../../models/table.model';

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
  dbTables!: Table[];
  tableColumnsNames!: string[];
  tableSelected: any;

  private dbId = history.state.dbId;

  constructor(
    private tableService: TableService,
    private fieldTypeService: FieldTypeService,
    private constraintService: ConstraintService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {}

  ngOnInit(): void {
    this.initFormGroup();

    this.initConstraints();
    this.initFieldTypes();

    this.initTablesByDbId();
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

  private initTablesByDbId() {
    this.tableService.getTablesByDbId(this.dbId).subscribe((res) => {
      this.dbTables = res.result;
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

  updateColumnsList(index: number) {}

  getTableColumnsName(index: number): string[] {
    const curTableName = this.columns
      .at(index)
      .get('foreignTable')
      ?.get('tableName')?.value;
    console.log(curTableName);
    return this.dbTables
      .filter((t) => {
        return t.name === curTableName;
      })
      .flatMap((t) => t.columns!.map((c) => c.name));
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
      foreignTable: this.formBuilder.group({
        tableName: '',
        columnName: '',
      }),
    });
    this.columns.push(columnForm);
  }

  // addForeignTable(): void {
  //   const foreignTableForm = this.formBuilder.group({
  //     tableName: '',
  //     columnName: '',
  //   });
  //   this.foreignTables.push(foreignTableForm);
  // }

  removeColumn(index: number): void {
    if (this.columns.length > 1) {
      this.columns.removeAt(index);
    }
  }

  submit(): void {
    console.log(this.form.getRawValue());
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

    // this.tableService.save(createTableRequest).subscribe((res) => {
    //   this.router.navigate(['dbs', this.dbId]);
    // });
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
