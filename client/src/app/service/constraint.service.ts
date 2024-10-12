import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BASE_URL } from '../constants';
import { ColumnConstraintsResponse } from '../models/response/ColumnConstraintsResponse';

@Injectable({
  providedIn: 'root',
})
export class ConstraintService {
  constructor(private http: HttpClient) {}

  getConstraintsByTableAndColumn(tableId: string, columnName: string) {
    return this.http.get<ColumnConstraintsResponse>(
      BASE_URL + '/constraints/getByTableAndColumn',
      {
        params: {
          tableId: tableId,
          columnName: columnName,
        },
      }
    );
  }
}
