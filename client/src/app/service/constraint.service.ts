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
  // private constraints$ = new BehaviorSubject<Constraint[]>([]);

  // get constraints() {
  //   return this.constraints$.getValue();
  // }

  // set constraints(val: Constraint[]) {
  //   this.constraints$.next(val);
  // }

  constructor(private http: HttpClient) {
    // this.fetchConstraints();
  }

  // private fetchConstraints(): void {
  //   this.http
  //     .get<ConstraintsResponse>(BASE_URL + '/constraints')
  //     .subscribe((res) => {
  //       console.log('constaints' + res);
  //       this.constraints = res.result;
  //     });
  // }

  // getConstraints2(): BehaviorSubject<Constraint[]> {
  //   return this.constraints$;
  // }

  getConstraints() {
    return this.http.get<ConstraintsResponse>(BASE_URL + '/constraints');
  }
}
