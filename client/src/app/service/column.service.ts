import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BASE_URL } from '../constants';
import { ColumnResponse } from '../models/response/ColumnResponse';
import { MessageResponse } from '../models/response/MessageResponse';

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
    return this.http.post<MessageResponse>(
      BASE_URL + `/columns/addColumn`,
      column,
      {
        params: { tableId: tableId },
      }
    );
  }

  rename(tableId: string, data: any) {
    return this.http.put<MessageResponse>(BASE_URL + `/columns/rename`, data, {
      params: {
        tableId: tableId,
      },
    });
  }

  modifyColumnType(tableId: string, data: any) {
    return this.http.put<MessageResponse>(
      BASE_URL + '/columns/modifyType',
      data,
      {
        params: {
          tableId: tableId,
        },
      }
    );
  }

  modifyDefVal(tableId: string, data: any) {
    return this.http.put<MessageResponse>(
      BASE_URL + '/columns/modifyDefVal',
      data,
      {
        params: {
          tableId: tableId,
        },
      }
    );
  }

  delete(tableId: number, columnName: string) {
    return this.http.post<MessageResponse>(
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

  addConstraints(tableId: string, data: any) {
    return this.http.post<MessageResponse>(
      BASE_URL + `/columns/addConstraints`,
      data,
      {
        params: {
          tableId: tableId,
        },
      }
    );
  }

  deleteConstraint(tableId: string, data: any) {
    return this.http.post<MessageResponse>(
      BASE_URL + `/columns/deleteConstraint`,
      data,
      {
        params: {
          tableId: tableId,
        },
      }
    );
  }
}
