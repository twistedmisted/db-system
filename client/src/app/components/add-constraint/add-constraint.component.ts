import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { Table } from '../../models/table.model';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule
} from '@angular/forms';
import { ErrorBlockComponent } from '../error-block/error-block.component';
import { ConstraintService } from '../../service/constraint.service';
import { Constraints } from '../../models/constraints.model';
import { ActivatedRoute, Router } from '@angular/router';
import { ColumnService } from '../../service/column.service';
import { MessageService } from '../../service/message.service';
import { TableService } from '../../service/table.service';

@Component({
  selector: 'app-add-constraint',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    ErrorBlockComponent,
  ],
  templateUrl: './add-constraint.component.html',
  styleUrl: './add-constraint.component.scss',
})
export class AddConstraintComponent implements OnInit {
  @Input() dbTables!: Table[];
  @Input() form!: FormGroup;
  @Input() selectedView: string = 'asForm';

  columnConstraints!: Constraints;

  private canAdd!: boolean;
  private tableId!: string;
  private dbId!: string;
  private columnName!: string;

  constructor(
    private formBuilder: FormBuilder,
    private constraintService: ConstraintService,
    private columnService: ColumnService,
    private activatedRoute: ActivatedRoute,
    private messageService: MessageService,
    private tableService: TableService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.selectedView === 'asForm') {
      this.tableId = this.activatedRoute.snapshot.paramMap.get('tableId')!;
      this.dbId = this.activatedRoute.snapshot.paramMap.get('dbId')!;
      this.columnName = history.state.columnName;
      this.tableService.getTablesByDbId(this.dbId).subscribe((res) => {
        this.dbTables = res.result.filter((t) => t.id !== Number(this.tableId));
        this.initForm();
      });
    }
  }

  initForm() {
    this.constraintService
      .getConstraintsByTableAndColumn(this.tableId, this.columnName)
      .subscribe((res) => {
        console.log(res.result);
        this.columnConstraints = res.result;
        const formConstraints = this.formBuilder.group({});
        this.form = this.formBuilder.group({ constraints: formConstraints });

        if (!this.columnConstraints.notNull) {
          formConstraints.addControl('notNull', new FormControl(false));
        }
        if (!this.columnConstraints.primaryKey) {
          formConstraints.addControl('primaryKey', new FormControl(false));
        }
        if (!this.columnConstraints.unique) {
          formConstraints.addControl('unique', new FormControl(false));
        }
        if (
          (this.columnConstraints.foreignTable === undefined ||
            this.columnConstraints.foreignTable === null) &&
          !this.columnConstraints.primaryKey
        ) {
          console.log('adding foreign table');
          formConstraints.addControl(
            'foreignTable',
            this.formBuilder.group({
              foreignKey: false,
              tableName: '',
              columnName: '',
            })
          );
        }

        this.canAdd = Object.keys(formConstraints.getRawValue()).length > 0;
      });
  }

  getTableColumnsName(): string[] {
    const curTableName = this.form.value.constraints.foreignTable.tableName;
    return this.dbTables
      .filter((t) => {
        return t.name === curTableName;
      })
      .flatMap((t) => t.columns!.map((c) => c.name));
  }

  canAddConstraint(): boolean {
    return this.canAdd;
  }

  submit() {
    let ft;
    if (this.form.getRawValue().constraints.foreignTable.foreignKey) {
      if (this.form.value.constraints.foreignTable.tableName === '') {
        this.messageService.openError([
          'Need to specify a foreign table name for column ' + this.columnName,
        ]);
        throw new Error(
          'Need to specify a foreign table name for column' + this.columnName
        );
      }
      if (this.form.value.constraints.foreignTable.columnName === '') {
        this.messageService.openError([
          'Need to specify a foreign column name for column ' + this.columnName,
        ]);
        throw new Error(
          'Need to specify a foreign column name for column ' + this.columnName
        );
      }
      const tableId = this.dbTables.find((t) => {
        return t.name === this.form.value.constraints.foreignTable['tableName'];
      })!.id;
      ft = Object.assign({}, this.form.value.constraints.foreignTable, {
        tableName: `table_${tableId}`,
      });
    }

    const constraintsToSave = Object.assign({}, this.form.value.constraints, {
      foreignTable: ft,
    });

    const valueToSave = Object.assign({}, this.form.value, {
      columnName: this.columnName,
      constraints: constraintsToSave,
    });

    this.columnService
      .addConstraints(this.tableId, valueToSave)
      .subscribe((res) => {
        this.messageService.openSuccess(res.message);
        this.router.navigate(['dbs', this.dbId, 'tables', this.tableId]);
      });
  }
}
