import { Component, OnInit } from '@angular/core';
import { DbListComponent } from '../db-list/db-list.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [DbListComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit {
  token!: string | null;

  constructor() {}

  ngOnInit(): void {
    this.token = localStorage.getItem('token');
  }
}
