import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BASE_URL } from '../constants';
import { MessageResponse } from '../models/response/MessageResponse';

@Injectable({
  providedIn: 'root',
})
export class QueryService {
  constructor(private http: HttpClient) {}

  getByPK(tableId: string, pkName: string, pkValue: any) {
    return this.http.get(BASE_URL + `/web-queries/getByPK/${tableId}`, {
      params: { name: pkName, value: pkValue },
    });
  }

  getAll(tableId: string) {
    return this.http.get<[]>(BASE_URL + `/web-queries/getAll/${tableId}`);
  }

  save(tableId: string, data: any) {
    return this.http.post<MessageResponse>(
      BASE_URL + `/web-queries/save/${tableId}`,
      data
    );
  }

  update(tableId: string, data: any) {
    return this.http.put<MessageResponse>(
      BASE_URL + `/web-queries/update/${tableId}`,
      data
    );
  }

  delete(tableId: string, rec: any) {
    return this.http.delete(BASE_URL + `/web-queries/delete/${tableId}`, {
      body: rec,
    });
  }

  executeCustomSelect(data: any) {
    return this.http.post<[]>(BASE_URL + `/web-queries/execute`, data);
  }
}
