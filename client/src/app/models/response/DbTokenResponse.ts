import { ServerResponse } from 'http';
import { DbToken } from '../dbtoken.model';

export interface DbTokenResponse extends ServerResponse {
  result: DbToken;
}
