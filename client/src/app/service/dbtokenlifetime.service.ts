import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DbTokenLifeTimesResponse } from '../models/response/DbTokenLifeTimesResponse';
import { BASE_URL } from '../constants';

@Injectable({
  providedIn: 'root',
})
export class DbTokenLifeTimeService {
  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<DbTokenLifeTimesResponse>(
      BASE_URL + '/dbTokenLifeTimes'
    );
  }
}
