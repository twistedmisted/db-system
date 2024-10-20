import { Component, OnInit } from '@angular/core';
import { DbService } from '../../service/db.service';
import { Db } from '../../models/db.model';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TableListComponent } from '../table-list/table-list.component';
import { CommonModule } from '@angular/common';
import { DbTokenInfoComponent } from '../db-token-info/db-token-info.component';
import { AllQueriesComponent } from '../all-queries/all-queries.component';

@Component({
  selector: 'app-db',
  standalone: true,
  imports: [
    TableListComponent,
    CommonModule,
    RouterModule,
    DbTokenInfoComponent,
    DbTokenInfoComponent,
    AllQueriesComponent,
  ],
  templateUrl: './db.component.html',
  styleUrl: './db.component.scss',
})
export class DbComponent implements OnInit {
  db!: Db;

  constructor(
    private dbService: DbService,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.dbService
      .getById(this.activatedRoute.snapshot.paramMap.get('id')!)
      .subscribe((res) => {
        this.db = res.result;
      });
  }

  deleteDbById() {
    this.dbService.delete(this.db.id!).subscribe((res) => {
      this.router.navigate(['dbs']).then(() => window.location.reload());
    });
  }
}
