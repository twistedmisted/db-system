import { DbToken } from './dbtoken.model';

export interface Db {
  id?: number;
  name: string;
  status: string;
  token: DbToken;
  createdAt: string;
}
