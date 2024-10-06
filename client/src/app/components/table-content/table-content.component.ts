import { Component, OnInit } from '@angular/core';
import { QueryService } from '../../service/query.service';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TableService } from '../../service/table.service';
import { Table } from '../../models/table.model';
import { Constraints } from '../../models/constraints.model';
import { constants } from 'buffer';
import { MessageService } from '../../service/message.service';

@Component({
  selector: 'app-table-content',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './table-content.component.html',
  styleUrl: './table-content.component.scss',
})
export class TableContentComponent implements OnInit {
  content!: [];
  columnsNames!: string[];
  table!: Table;
  allowedToChange!: boolean;
  primaryKeyColumnName!: string;

  private tableId: string;

  constructor(
    private queryService: QueryService,
    private tableService: TableService,
    private activatedRoute: ActivatedRoute,
    private messageService: MessageService
  ) {
    this.tableId = this.activatedRoute.snapshot.paramMap.get('tableId')!;
  }

  ngOnInit(): void {
    this.queryService.getAll(this.tableId).subscribe((res) => {
      if (res && res.length > 0) {
        this.columnsNames = Object.getOwnPropertyNames(res.at(0));
        this.content = res;
      }
    });

    this.tableService.getTableById(this.tableId).subscribe((res) => {
      this.table = res.result;
      let pk = this.table.columns!.find((c) =>
        this.isAllowedToChange(c.constraints)
      );
      if (pk) {
        this.allowedToChange = true;
        this.primaryKeyColumnName = pk.name;
      }
    });
  }

  isAllowedToChange(constraints: Constraints): boolean {
    return constraints.primaryKey;
  }

  delete(rec: any) {
    console.log(rec);
    this.queryService.delete(this.tableId, rec).subscribe(() => {
      this.messageService.openSuccess('Record successfully removed');
      this.ngOnInit();
    });
  }
}
