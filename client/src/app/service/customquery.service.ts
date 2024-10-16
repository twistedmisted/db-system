import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MessageResponse } from '../models/response/MessageResponse';
import { BASE_URL } from '../constants';
import { CustomQueryResponse } from '../models/response/CustomQueryResponse';
import { CustomQueriesResponse } from '../models/response/CustomQueriesResponse';

@Injectable({
  providedIn: 'root',
})
export class CustomQueryService {
  constructor(private http: HttpClient) {}

  getById(id: number, dbId: string) {
    return this.http.get<CustomQueryResponse>(BASE_URL + `/customQuery/${id}`, {
      params: {
        dbId: dbId,
      },
    });
  }

  getAll(dbId: string) {
    return this.http.get<CustomQueriesResponse>(BASE_URL + '/customQuery', {
      params: {
        dbId: dbId,
      },
    });
  }

  saveQuery(data: any) {
    return this.http.post<MessageResponse>(BASE_URL + `/customQuery`, data);
  }

  update(data: any) {
    return this.http.put<MessageResponse>(BASE_URL + `/customQuery`, data);
  }

  delete(id: number, dbId: string) {
    return this.http.delete<MessageResponse>(BASE_URL + `/customQuery/${id}`, {
      params: {
        dbId: dbId,
      },
    });
  }
}
