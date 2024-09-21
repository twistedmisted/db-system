import { Component, OnInit } from '@angular/core';
import { TableService } from '../../service/table.service';
import { Table } from '../../models/table.model';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Constraint } from '../../models/constraint.model';
import { ColumnService } from '../../service/column.service';
import { ApiInfoComponent } from '../api-info/api-info.component';

@Component({
  selector: 'app-table',
  standalone: true,
  imports: [CommonModule, RouterModule, ApiInfoComponent],
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
    private activatedRoute: ActivatedRoute
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
      this.ngOnInit();
    });
  }

  deleteColumnByName(columnName: string) {
    this.columnService.delete(this.table.id!, columnName).subscribe((res) => {
      console.log(res);
      this.ngOnInit();
    });
  }
}
