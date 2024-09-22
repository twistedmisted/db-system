import { Component, OnInit } from '@angular/core';
import { TableService } from '../../service/table.service';
import { Table } from '../../models/table.model';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Constraint } from '../../models/constraint.model';
import { ColumnService } from '../../service/column.service';
import { ApiInfoComponent } from '../api-info/api-info.component';
import { MessageResponse } from '../../models/response/MessageResponse';
import { MessageService } from '../../service/message.service';
import { ErrorBlockComponent } from '../error-block/error-block.component';

@Component({
  selector: 'app-table',
  standalone: true,
  imports: [CommonModule, RouterModule, ApiInfoComponent, ErrorBlockComponent],
  templateUrl: './table.component.html',
  styleUrl: './table.component.scss',
})
export class TableComponent implements OnInit {
  table!: Table;
  constraints!: any;
  dbId!: string;

  constructor(
    private tableService: TableService,
    private columnService: ColumnService,
    private messageSerivce: MessageService,
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
        this.table = res.result;
        this.constraints = this.table.columns!.map((c) =>
          c.constraints.filter((v) => v.statusValue).map((v) => v.value)
        );
      });
  }

  deleteTableById(tableId: number) {
    this.tableService.delete(tableId).subscribe((res) => {
      this.router.navigate(['/dbs', this.dbId]);
    });
  }

  deleteColumnByName(columnName: string) {
    this.columnService.delete(this.table.id!, columnName).subscribe((res) => {
      this.messageSerivce.openSuccess(res.message);
      this.ngOnInit();
    });
  }
}
