import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BASE_URL } from '../constants';
import { IsTokenValidResponse } from '../models/response/IsTokenValidResponse';
import {MessageResponse} from "../models/response/MessageResponse";
import {Emitters} from "../emitters/emitters";

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

  login(data: any) {
    return this.http.post(BASE_URL + '/auth/login', data);
  }

  register(data: any) {
    return this.http.post<MessageResponse>(BASE_URL + '/auth/register', data);
  }

  logout() {
    return this.http.post(BASE_URL + '/auth/logout', {});
  }
}
