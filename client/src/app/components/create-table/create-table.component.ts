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
import { BehaviorSubject } from 'rxjs';
import { TableService } from '../../service/table.service';
import { CreateTableRequest } from '../../models/request/CreateTableRequest';
import { ErrorBlockComponent } from '../error-block/error-block.component';
import { Table } from '../../models/table.model';
import { MessageService } from '../../service/message.service';

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
  dbTables!: Table[];

  private dbId = history.state.dbId;

  constructor(
    private tableService: TableService,
    private fieldTypeService: FieldTypeService,
    private messageService: MessageService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {}

  ngOnInit(): void {
    this.initFormGroup();

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

  private initTablesByDbId() {
    this.tableService.getTablesByDbId(this.dbId).subscribe((res) => {
      this.dbTables = res.result;
      this.addColumn();
    });
  }

  get columns(): FormArray {
    return this.form.get('columns') as FormArray;
  }

  getTableColumnsName(index: number): string[] {
    const curTableName = this.columns
      .at(index)
      .get('constraints')!
      .get('foreignTable')
      ?.get('tableName')?.value;
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
      constraints: this.formBuilder.group({
        notNull: false,
        primaryKey: false,
        identity: false,
        autoIncrement: false,
        unique: false,
        foreignTable: this.formBuilder.group({
          foreignKey: false,
          tableName: '',
          columnName: '',
        }),
      }),
      defaultValue: '',
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
      let ft;
      if (c.constraints.foreignTable.foreignKey) {
        if (c.constraints.foreignTable.tableName === '') {
          this.messageService.openError([
            'Need to specify a foreign table name for column ' + c.name,
          ]);
          throw new Error('Need to specify a table name ' + c.name);
        }
        if (c.constraints.foreignTable.columnName === '') {
          this.messageService.openError([
            'Need to specify a foreign column name for column ' + c.name,
          ]);
          throw new Error(
            'Need to specify a foreign column name for column ' + c.name
          );
        }
        const tableId = this.dbTables.find((t) => {
          return t.name === c.constraints.foreignTable['tableName'];
        })!.id;
        ft = Object.assign({}, c.constraints.foreignTable, {
          tableName: `table_${tableId}`,
        });
      }
      return Object.assign({}, c, {
        foreignTable: ft,
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

    this.tableService.save(createTableRequest).subscribe((res) => {
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
