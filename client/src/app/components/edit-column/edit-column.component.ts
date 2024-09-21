import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CommonModule, Location } from '@angular/common';
import { ConstraintService } from '../../service/constraint.service';
import { FieldTypeService } from '../../service/fieldtype.service';
import { ColumnService } from '../../service/column.service';
import { Constraint } from '../../models/constraint.model';
import { BehaviorSubject, forkJoin } from 'rxjs';
import { Column } from '../../models/column.model';
import { table } from 'console';

@Component({
  selector: 'app-edit-column',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './edit-column.component.html',
  styleUrl: './edit-column.component.scss',
})
export class EditColumnComponent implements OnInit {
  form!: FormGroup;
  fieldTypes$ = new BehaviorSubject<string[]>([]);
  constraints!: Constraint[];
  tableId!: string;
  columnName!: string;
  column!: Column;

  constructor(
    private constraintService: ConstraintService,
    private fieldTypeService: FieldTypeService,
    private columnService: ColumnService,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private _location: Location
  ) {}

  ngOnInit(): void {
    this.tableId = this.activatedRoute.snapshot.paramMap.get('tableId')!;
    this.columnName = this.activatedRoute.snapshot.paramMap.get('columnName')!;

    this.initFieldTypes();

    // if (this.column && this.constraints) {
    //   this.initForm();
    // }

    forkJoin({
      column: this.initColumn(),
      constraints: this.initConstraints(),
    }).subscribe({
      next: (res) => {
        this.column = res.column.result;
        this.constraints = res.constraints.result;
        this.initForm();
      },
    });
  }

  private initFieldTypes() {
    this.fieldTypes$ = this.fieldTypeService.getFieldTypes();
  }

  private initConstraints() {
    return this.constraintService.getConstraints();
  }

  private initColumn() {
    return this.columnService.getColumnByName(this.tableId, this.columnName);
  }

  private initForm(): void {
    this.form = this.formBuilder.group({
      name: [
        this.column.name,
        [
          Validators.required,
          Validators.minLength(1),
          Validators.maxLength(255),
        ],
      ],
      columnType: this.formBuilder.group({
        type: this.column.type.type,
        value: this.column.type.value,
      }),
      columnConstraints: this.formBuilder.array(
        this.constraints.map((key) =>
          this.column.constraints.find((c) => c.value === key.value)
        )
      ),
    });
  }

  submit(): void {
    const constraintsToSave = this.form.value.columnConstraints
      .map((v: any, i: number) => v && this.constraints[i])
      .filter((v: any) => !!v);

    const valueToSave = Object.assign({}, this.form.value, {
      columnConstraints: constraintsToSave,
    });

    console.log(valueToSave);

    // this.columnService.updateColumn(this.tableId, valueToSave).subscribe((res) => {
    //   console.log(res);
    //   this._location.back();
    // });
  }
}
