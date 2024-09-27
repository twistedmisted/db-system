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

@Component({
  selector: 'app-table',
  standalone: true,
  imports: [CommonModule, RouterModule, ApiInfoComponent, ErrorBlockComponent],
  templateUrl: './table.component.html',
  styleUrl: './table.component.scss',
})
export class TableComponent implements OnInit {
  table!: Table;
  constraints!: string;
  dbId!: string;

  constructor(
    private tableService: TableService,
    private columnService: ColumnService,
    private messageService: MessageService,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.dbId = this.activatedRoute.snapshot.paramMap.get('dbId')!;
    this.fetchTable();
  }

  fetchTable(): void {
    this.tableService
      .getTableById(this.activatedRoute.snapshot.paramMap.get('tableId')!)
      .subscribe((res) => {
        console.log(res);
        this.table = res.result;
      });
  }

  getConstraints(column: Column): string[] {
    let constraints: string[] = [];
    const constraintsObj: Constraints = column.constraints;
    if (constraintsObj.primaryKey) {
      constraints.push('PRIMARY KEY');
    }
    if (constraintsObj.foreignTable && constraintsObj.foreignTable.foreignKey) {
      const foreignTable: ForeignTable = constraintsObj.foreignTable;
      console.log('here con');
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
    this.tableService.delete(tableId).subscribe((res) => {
      this.router.navigate(['/dbs', this.dbId]);
    });
  }

  deleteColumnByName(columnName: string) {
    this.columnService.delete(this.table.id!, columnName).subscribe((res) => {
      this.messageService.openSuccess(res.message);
      this.ngOnInit();
    });
  }
}
