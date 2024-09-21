import { Constraint } from './constraint.model';
import { FieldType } from './fieldtype.model';

export interface Column {
  name: string;
  type: FieldType;
  constraints: Constraint[];
}
