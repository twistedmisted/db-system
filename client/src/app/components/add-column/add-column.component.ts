import { CommonModule, Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ConstraintService } from '../../service/constraint.service';
import { FieldTypeService } from '../../service/fieldtype.service';
import { BehaviorSubject } from 'rxjs';
import { Constraint } from '../../models/constraint.model';
import { ColumnService } from '../../service/column.service';
import { MessageService } from '../../service/message.service';
import { ErrorBlockComponent } from '../error-block/error-block.component';
import { Table } from '../../models/table.model';
import { TableService } from '../../service/table.service';

@Component({
  selector: 'app-add-column',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    ErrorBlockComponent,
  ],
  templateUrl: './add-column.component.html',
  styleUrl: './add-column.component.scss',
})
export class AddColumnComponent implements OnInit {
  form!: FormGroup;
  fieldTypes$ = new BehaviorSubject<string[]>([]);
  constraints!: Constraint[];
  private tableId!: string;
  private dbId!: string;
  dbTables!: Table[];

  constructor(
    private tableService: TableService,
    private constraintService: ConstraintService,
    private fieldTypeService: FieldTypeService,
    private columnService: ColumnService,
    private messageService: MessageService,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private _location: Location
  ) {}

  ngOnInit(): void {
    this.tableId = this.activatedRoute.snapshot.paramMap.get('tableId')!;
    this.dbId = this.activatedRoute.snapshot.paramMap.get('dbId')!;

    this.initFieldTypes();
    this.initConstraints();

    this.initTablesByDbId();
  }

  private initForm(): void {
    this.form = this.formBuilder.group({
      name: [
        '',
        [
          Validators.required,
          Validators.minLength(1),
          Validators.maxLength(255),
        ],
      ],
      columnType: this.formBuilder.group({
        type: '',
        value: null,
      }),
      columnConstraints: this.formBuilder.array(
        Object.keys(this.constraints).map((key) => false)
      ),
      foreignTable: this.formBuilder.group({
        tableName: '',
        columnName: '',
      }),
      defaultValue: '',
    });
  }

  private initFieldTypes() {
    this.fieldTypes$ = this.fieldTypeService.getFieldTypes();
  }

  private initConstraints() {
    this.constraintService.getConstraints().subscribe((res) => {
      this.constraints = res.result;
      this.initForm();
    });
  }

  private initTablesByDbId() {
    this.tableService.getTablesByDbId(this.dbId).subscribe((res) => {
      this.dbTables = res.result.filter((t) => t.id !== Number(this.tableId));
    });
  }

  getTableColumnsName(): string[] {
    const curTableName = this.form.value.foreignTable.tableName;
    return this.dbTables
      .filter((t) => {
        return t.name === curTableName;
      })
      .flatMap((t) => t.columns!.map((c) => c.name));
  }

  submit(): void {
    const constraintsToSave = this.form.value.columnConstraints
      .map((v: any, i: number) => v && this.constraints[i])
      .filter((v: any) => !!v);

    let ft;
    if (constraintsToSave.find((e: Constraint) => e.value === 'FOREIGN KEY')) {
      if (this.form.value.foreignTable.tableName === '') {
        this.messageService.openError([
          'Need to specify a foreign table name for column ' +
            this.form.value.name,
        ]);
        throw new Error('Need to specify a table name ' + this.form.value.name);
      }
      if (this.form.value.foreignTable.columnName === '') {
        this.messageService.openError([
          'Need to specify a foreign column name for column ' +
            this.form.value.name,
        ]);
        throw new Error(
          'Need to specify a foreign column name for column ' +
            this.form.value.name
        );
      }
      const tableId = this.dbTables.find((t) => {
        return t.name === this.form.value.foreignTable['tableName'];
      })!.id;
      ft = Object.assign({}, this.form.value.foreignTable, {
        tableName: `table_${tableId}`,
      });
    }

    const valueToSave = Object.assign({}, this.form.value, {
      columnConstraints: constraintsToSave,
      foreignTable: ft,
    });

    this.columnService.addColumn(this.tableId, valueToSave).subscribe((res) => {
      this.messageService.openSuccess(res.message);
      this._location.back();
    });
  }
}
