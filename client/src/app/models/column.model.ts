import { Constraint } from './constraint.model';
import { FieldType } from './fieldtype.model';
import { ForeignTable } from './foreigntable.model';

export interface Column {
  name: string;
  type: FieldType;
  constraints: Constraint[];
  foreignTable: ForeignTable;
  defaultValue: string;
}
