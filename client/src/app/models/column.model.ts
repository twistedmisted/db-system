import { Constraints } from './constraints.model';
import { FieldType } from './fieldtype.model';
import { ForeignTable } from './foreigntable.model';

export interface Column {
  name: string;
  columnType: FieldType;
  defaultValue: string;
  constraints: Constraints;
}
