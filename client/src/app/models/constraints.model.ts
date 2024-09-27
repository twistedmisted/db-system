import { ForeignTable } from './foreigntable.model';

export interface Constraints {
  primaryKey: boolean;
  identity: boolean;
  autoIncrement: boolean;
  notNull: boolean;
  unique: boolean;
  foreignTable: ForeignTable;
}
