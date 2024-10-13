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
import { FieldTypeService } from '../../service/fieldtype.service';
import { BehaviorSubject } from 'rxjs';
import { ColumnService } from '../../service/column.service';
import { MessageService } from '../../service/message.service';
import { ErrorBlockComponent } from '../error-block/error-block.component';
import { Table } from '../../models/table.model';
import { TableService } from '../../service/table.service';
import { AddConstraintComponent } from '../add-constraint/add-constraint.component';

@Component({
  selector: 'app-add-column',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    ErrorBlockComponent,
    AddConstraintComponent,
  ],
  templateUrl: './add-column.component.html',
  styleUrl: './add-column.component.scss',
})
export class AddColumnComponent implements OnInit {
  form!: FormGroup;
  fieldTypes$ = new BehaviorSubject<string[]>([]);
  private tableId!: string;
  private dbId!: string;
  dbTables!: Table[];

  constructor(
    private tableService: TableService,
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
  }

  private initFieldTypes() {
    this.fieldTypes$ = this.fieldTypeService.getFieldTypes();
  }

  private initTablesByDbId() {
    this.tableService.getTablesByDbId(this.dbId).subscribe((res) => {
      this.dbTables = res.result.filter((t) => t.id !== Number(this.tableId));
      this.initForm();
    });
  }

  submit(): void {
    let ft;
    if (this.form.getRawValue().constraints.foreignTable.foreignKey) {
      if (this.form.value.constraints.foreignTable.tableName === '') {
        this.messageService.openError([
          'Need to specify a foreign table name for column ' +
            this.form.value.name,
        ]);
        throw new Error(
          'Need to specify a foreign table name ' + this.form.value.name
        );
      }
      if (this.form.value.constraints.foreignTable.columnName === '') {
        this.messageService.openError([
          'Need to specify a foreign column name for column ' +
            this.form.value.name,
        ]);
        throw new Error(
          'Need to specify a foreign column name for column ' +
            this.form.value.name
        );
      }
      // const tableId = this.dbTables.find((t) => {
      //   return t.name === this.form.value.constraints.foreignTable['tableName'];
      // })!.id;
      // ft = Object.assign({}, this.form.value.constraints.foreignTable, {
      //   tableName: `table_${tableId}`,
      // });
    }

    // const constraintsToSave = Object.assign({}, this.form.value.constraints, {
    //   foreignTable: ft,
    // });
    // const valueToSave = Object.assign({}, this.form.value, {
    //   constraints: constraintsToSave,
    // });

    const valueToSave = this.form.getRawValue();

    console.log(valueToSave);

    this.columnService.addColumn(this.tableId, valueToSave).subscribe((res) => {
      this.messageService.openSuccess(res.message);
      this._location.back();
    });
  }
}
