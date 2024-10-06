import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BASE_URL } from '../constants';
import { MessageResponse } from '../models/response/MessageResponse';

@Injectable({
  providedIn: 'root',
})
export class QueryService {
  constructor(private httpClient: HttpClient) {}

  getByPK(tableId: string, pkName: string, pkValue: any) {
    return this.httpClient.get(BASE_URL + `/web-queries/getByPK/${tableId}`, {
      params: { name: pkName, value: pkValue },
    });
  }

  getAll(tableId: string) {
    return this.httpClient.get<[]>(BASE_URL + `/web-queries/getAll/${tableId}`);
  }

  save(tableId: string, data: any) {
    return this.httpClient.post<MessageResponse>(
      BASE_URL + `/web-queries/save/${tableId}`,
      data
    );
  }

  update(tableId: string, data: any) {
    return this.httpClient.put<MessageResponse>(
      BASE_URL + `/web-queries/update/${tableId}`,
      data
    );
  }

  delete(tableId: string, rec: any) {
    return this.httpClient.delete(BASE_URL + `/web-queries/delete/${tableId}`, {
      body: rec,
    });
  }
}
