import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BASE_URL } from '../constants';
import { TablesResponse } from '../models/response/TablesResponse';
import { CreateTableRequest } from '../models/request/CreateTableRequest';
import { TableResponse } from '../models/response/TableResponse';
import { Table } from '../models/table.model';

@Injectable({
  providedIn: 'root',
})
export class TableService {
  constructor(private http: HttpClient) {}

  getTableById(tableId: string) {
    console.log('Sending request ' + tableId);
    return this.http.get<TableResponse>(BASE_URL + `/tables/${tableId}`);
  }

  getTablesByDbId(dbId: string) {
    return this.http.get<TablesResponse>(BASE_URL + `/tables/db/${dbId}`);
  }

  save(table: CreateTableRequest) {
    return this.http.post(BASE_URL + '/tables', table);
  }

  update(table: Table) {
    return this.http.put(BASE_URL + '/tables', table);
  }

  delete(tableId: number) {
    return this.http.delete(BASE_URL + `/tables/${tableId}`);
  }
}
