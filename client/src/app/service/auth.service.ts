import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BASE_URL } from '../constants';
import { IsTokenValidResponse } from '../models/response/IsTokenValidResponse';
import { ConstraintsResponse } from '../models/response/ConstraintsResponse';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private http: HttpClient) {}

  validateToken() {
    return this.http.get<IsTokenValidResponse>(
      BASE_URL + '/auth/validate-token'
    );
  }

  logout() {
    return this.http.post(BASE_URL + '/auth/logout', {});
  }
}
