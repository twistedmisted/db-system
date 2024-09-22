import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { TableService } from '../../service/table.service';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Table } from '../../models/table.model';
import { CommonModule } from '@angular/common';
import { table } from 'console';

@Component({
  selector: 'app-table-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './table-list.component.html',
  styleUrl: './table-list.component.scss',
})
export class TableListComponent implements OnInit {
  tables!: Table[];
  dbId!: string;

  constructor(
    private tableService: TableService,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.dbId = this.activatedRoute.snapshot.paramMap.get('id')!;
    this.tableService.getTablesByDbId(this.dbId).subscribe((res) => {
      this.tables = res.result;
    });
  }
}
