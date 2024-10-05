import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BASE_URL } from '../constants';
import { MessageResponse } from '../models/response/MessageResponse';

@Injectable({
  providedIn: 'root',
})
export class QueryService {
  constructor(private httpClient: HttpClient) {}

  getAll(tableId: string) {
    return this.httpClient.get(BASE_URL + `/web-queries/getAll/${tableId}`);
  }

  save(tableId: string, data: any) {
    return this.httpClient.post<MessageResponse>(
      BASE_URL + `/web-queries/save/${tableId}`,
      data
    );
  }
}
