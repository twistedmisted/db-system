import { Column } from './column.model';

export interface Table {
  id?: number;
  name: string;
  columns?: Column[];
}
