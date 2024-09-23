import { Component, OnInit } from '@angular/core';
import { DbService } from '../../service/db.service';
import { BehaviorSubject } from 'rxjs';
import { Db } from '../../models/db.model';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-db-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './db-list.component.html',
  styleUrl: './db-list.component.scss',
})
export class DbListComponent implements OnInit {
  dbs$ = new BehaviorSubject<Db[]>([]);

  constructor(private dbService: DbService) {}

  ngOnInit(): void {
    this.dbs$ = this.dbService.getDbs();
  }
}
