import { Component, Input, OnInit } from '@angular/core';
import { BASE_URL } from '../../constants';

@Component({
  selector: 'app-api-info',
  standalone: true,
  imports: [],
  templateUrl: './api-info.component.html',
  styleUrl: './api-info.component.scss',
})
export class ApiInfoComponent implements OnInit {
  @Input() tableName!: string;

  getByFieldLink!: string;
  getAllLink!: string;
  createLink!: string;
  updateLink!: string;
  deleteLink!: string;

  ngOnInit(): void {
    const queiries = '/queries';
    this.getByFieldLink =
      BASE_URL + '/' + this.tableName + queiries + '/getByColumn';
    this.getAllLink = BASE_URL + '/' + this.tableName + queiries + '/getAll';
    this.createLink = BASE_URL + '/' + this.tableName + queiries + '/save';
    this.updateLink = BASE_URL + '/' + this.tableName + queiries + '/update';
    this.deleteLink = BASE_URL + '/' + this.tableName + queiries + '/delete';
  }
}
