import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { CustomQueryService } from '../../service/customquery.service';
import { CustomQuery } from '../../models/customquery.model';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { MessageService } from '../../service/message.service';

@Component({
  selector: 'app-all-queries',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './all-queries.component.html',
  styleUrl: './all-queries.component.scss',
})
export class AllQueriesComponent implements OnInit {
  queries!: CustomQuery[];
  dbId: string;

  constructor(
    private customQueryService: CustomQueryService,
    private messageService: MessageService,
    private activatedRoute: ActivatedRoute
  ) {
    this.dbId = this.activatedRoute.snapshot.paramMap.get('dbId')!;
  }

  ngOnInit(): void {
    this.customQueryService.getAll(this.dbId).subscribe((res) => {
      this.queries = res.result;
    });
  }

  deleteQuery(id: number) {
    this.customQueryService.delete(id, this.dbId).subscribe((res) => {
      this.messageService.openSuccess(res.message);
      this.ngOnInit();
    });
  }
}
