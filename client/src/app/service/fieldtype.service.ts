import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { FieldType } from '../models/fieldtype.model';
import { BASE_URL } from '../constants';
import { FieldTypesResponse } from '../models/response/FieldTypesResponse';
import { DbResponse } from '../models/response/DbResponse';

@Injectable({
  providedIn: 'root',
})
export class FieldTypeService {
  private fieldTypes$ = new BehaviorSubject<string[]>([]);

  get fieldTypes() {
    return this.fieldTypes$.getValue();
  }

  set fieldTypes(val: string[]) {
    this.fieldTypes$.next(val);
  }

  constructor(private http: HttpClient) {
    this.fetchFieldTypes();
  }

  private fetchFieldTypes() {
    this.http
      .get<FieldTypesResponse>(BASE_URL + '/fieldTypes')
      .subscribe((res) => {
        this.fieldTypes = res.result;
      });
  }

  getFieldTypes(): BehaviorSubject<string[]> {
    return this.fieldTypes$;
  }
}
