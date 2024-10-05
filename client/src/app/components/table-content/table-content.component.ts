import { Component, OnInit } from '@angular/core';
import { QueryService } from '../../service/query.service';
import { ActivatedRoute, RouterModule } from '@angular/router';

@Component({
  selector: 'app-table-content',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './table-content.component.html',
  styleUrl: './table-content.component.scss',
})
export class TableContentComponent implements OnInit {
  content!: [];

  constructor(
    private queryService: QueryService,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.queryService
      .getAll(this.activatedRoute.snapshot.paramMap.get('tableId')!)
      .subscribe((res) => console.log(res));
  }
}
