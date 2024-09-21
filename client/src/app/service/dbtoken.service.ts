import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DbTokenResponse } from '../models/response/DbTokenResponse';
import { BASE_URL } from '../constants';

@Injectable({
  providedIn: 'root',
})
export class DbtokenService {
  constructor(private http: HttpClient) {}

  getById(id: string) {
    return this.http.get<DbTokenResponse>(BASE_URL + `/dbToknes/{id}`);
  }

  create(token: any) {
    return this.http.post(BASE_URL + '/dbTokens', token);
  }
}
