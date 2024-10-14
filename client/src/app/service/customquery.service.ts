import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MessageResponse } from '../models/response/MessageResponse';
import { BASE_URL } from '../constants';

@Injectable({
  providedIn: 'root',
})
export class CustomQueryService {
  constructor(private http: HttpClient) {}

  saveQuery(data: any) {
    return this.http.post<MessageResponse>(BASE_URL + `/customQuery`, data);
  }
}
