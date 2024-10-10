import { Component, OnInit } from '@angular/core';
import { TableService } from '../../service/table.service';
import { Table } from '../../models/table.model';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ColumnService } from '../../service/column.service';
import { ApiInfoComponent } from '../api-info/api-info.component';
import { MessageService } from '../../service/message.service';
import { ErrorBlockComponent } from '../error-block/error-block.component';
import { Column } from '../../models/column.model';
import { Constraints } from '../../models/constraints.model';
import { ForeignTable } from '../../models/foreigntable.model';
import { FieldTypeService } from '../../service/fieldtype.service';
import { BehaviorSubject } from 'rxjs';
import { ColumnTypesComponent } from '../column-types/column-types.component';
import { ConstraintListComponent } from '../constraint-list/constraint-list.component';

@Component({
  selector: 'app-table',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ApiInfoComponent,
    ErrorBlockComponent,
    ColumnTypesComponent,
    ConstraintListComponent,
  ],
  templateUrl: './table.component.html',
  styleUrl: './table.component.scss',
})
export class TableComponent implements OnInit {
  table!: Table;
  constraints!: string;
  fieldTypes$ = new BehaviorSubject<string[]>([]);

  private dbId: string;
  tableId: string;

  private oldVal: any = null;

  constructor(
    private tableService: TableService,
    private columnService: ColumnService,
    private messageService: MessageService,
    private columnTypeService: FieldTypeService,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {
    this.dbId = this.activatedRoute.snapshot.paramMap.get('dbId')!;
    this.tableId = this.activatedRoute.snapshot.paramMap.get('tableId')!;
  }

  ngOnInit(): void {
    this.fetchTable();
    this.initFieldTypes();
  }

  fetchTable(): void {
    this.tableService.getTableById(this.tableId).subscribe((res) => {
      this.table = res.result;
    });
  }

  private initFieldTypes() {
    this.fieldTypes$ = this.columnTypeService.getFieldTypes();
  }

  getConstraints(column: Column): string[] {
    let constraints: string[] = [];
    const constraintsObj: Constraints = column.constraints;
    if (constraintsObj.primaryKey) {
      constraints.push('PRIMARY KEY');
    }
    if (constraintsObj.foreignTable && constraintsObj.foreignTable.foreignKey) {
      const foreignTable: ForeignTable = constraintsObj.foreignTable;
      constraints.push(
        `FOREIGN KEY REFERENCES ${foreignTable.tableName}(${foreignTable.columnName})`
      );
    }
    if (constraintsObj.identity) {
      constraints.push('IDENTITY');
    }
    if (constraintsObj.autoIncrement) {
      constraints.push('AUTO INCREMENT');
    }
    if (constraintsObj.notNull) {
      constraints.push('NOT NULL');
    }
    if (constraintsObj.unique) {
      constraints.push('UNIQUE');
    }
    return constraints;
  }

  deleteTableById(tableId: number) {
    this.tableService.delete(tableId).subscribe(() => {
      this.router.navigate(['/dbs', this.dbId]);
    });
  }

  deleteColumnByName(columnName: string) {
    this.columnService.delete(this.table.id!, columnName).subscribe((res) => {
      this.messageService.openSuccess(res.message);
      this.ngOnInit();
    });
  }

  showInput(id: string): void {
    const element: HTMLInputElement = document.getElementById(
      id
    )! as HTMLInputElement;
    element.readOnly = false;
    element.classList.remove('form-control-plaintext');
    element.classList.add('form-control');
    if (this.oldVal === null) {
      this.oldVal = element.value;
    }
  }

  onEnter(id: string): void {
    const element: HTMLInputElement = document.getElementById(
      id
    )! as HTMLInputElement;
    this.columnService
      .rename(this.tableId, {
        oldName: this.oldVal,
        newName: element.value,
      })
      .subscribe((res) => {
        this.messageService.openSuccess(res.message);
        this.oldVal = element.value;
        element.blur();
      });
  }

  lostFocus(id: string) {
    console.log('lost focus');
    const element: HTMLInputElement = document.getElementById(
      id
    )! as HTMLInputElement;
    element.readOnly = true;
    element.classList.remove('form-control');
    element.classList.add('form-control-plaintext');
    element.value = this.oldVal;
    this.oldVal = null;
  }

  deleteConstraint(data: any) {
    console.log(data);
    this.columnService.deleteConstraint(this.tableId, data).subscribe((res) => {
      this.messageService.openSuccess(res.message);
      this.ngOnInit();
    });
  }
}
