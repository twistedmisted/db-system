import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Constraint } from '../models/constraint.model';
import { ConstraintsResponse } from '../models/response/ConstraintsResponse';
import { BASE_URL } from '../constants';

@Injectable({
  providedIn: 'root',
})
export class ConstraintService {
  constructor(private http: HttpClient) {}

  getConstraints() {
    return this.http.get<ConstraintsResponse>(BASE_URL + '/constraints');
  }
}
