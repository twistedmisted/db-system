import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BASE_URL } from '../constants';
import { ColumnResponse } from '../models/response/ColumnResponse';

@Injectable({
  providedIn: 'root',
})
export class ColumnService {
  constructor(private http: HttpClient) {}

  getColumnByName(tableId: string, name: string) {
    return this.http.get<ColumnResponse>(BASE_URL + `/columns/${name}`, {
      params: { tableId: tableId },
    });
  }

  addColumn(tableId: string, column: any) {
    return this.http.post(BASE_URL + `/columns/addColumn`, column, {
      params: { tableId: tableId },
    });
  }

  delete(tableId: number, columnName: string) {
    return this.http.post(
      BASE_URL + `/columns/deleteColumn`,
      {
        name: columnName,
      },
      {
        params: {
          tableId: tableId,
        },
      }
    );
  }
}
