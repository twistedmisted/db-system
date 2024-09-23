import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Db } from '../models/db.model';
import { DbsResponse } from '../models/response/DbsResponse';
import { BASE_URL } from '../constants';
import { DbResponse } from '../models/response/DbResponse';
import { text } from 'stream/consumers';

@Injectable({
  providedIn: 'root',
})
export class DbService {
  private dbs$ = new BehaviorSubject<Db[]>([]);

  get dbs() {
    return this.dbs$.getValue();
  }

  set dbs(val: Db[]) {
    this.dbs$.next(val);
  }

  constructor(private http: HttpClient) {
    this.fetchDbs();
  }

  private fetchDbs() {
    this.http
      .get<DbsResponse>(BASE_URL + '/dbs', {
        params: {
          pageNum: 0,
          pageSize: 1000,
        },
      })
      .subscribe((res) => {
        this.dbs = res.result.content;
      });
  }

  getById(id: string) {
    return this.http.get<DbResponse>(BASE_URL + `/dbs/${id}`);
  }

  getDbs(): BehaviorSubject<Db[]> {
    return this.dbs$;
  }

  save(db: any) {
    return this.http.post(BASE_URL + '/dbs', db);
  }

  updateName(dbId: string, dbNewName: any) {
    return this.http.put(BASE_URL + `/dbs/${dbId}`, dbNewName);
  }

  delete(dbId: number) {
    return this.http.delete<string>(BASE_URL + `/dbs/${dbId}`);
  }
}
