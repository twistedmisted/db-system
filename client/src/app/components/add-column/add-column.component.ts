import { CommonModule, Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ConstraintService } from '../../service/constraint.service';
import { FieldTypeService } from '../../service/fieldtype.service';
import { BehaviorSubject } from 'rxjs';
import { Constraint } from '../../models/constraint.model';
import { ColumnService } from '../../service/column.service';

@Component({
  selector: 'app-add-column',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './add-column.component.html',
  styleUrl: './add-column.component.scss',
})
export class AddColumnComponent implements OnInit {
  form!: FormGroup;
  fieldTypes$ = new BehaviorSubject<string[]>([]);
  constraints!: Constraint[];
  tableId!: string;

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

    this.initFieldTypes();
    this.initConstraints();
  }

  private initForm(): void {
    this.form = this.formBuilder.group({
      name: [
        '',
        [
          Validators.required,
          Validators.minLength(1),
          Validators.maxLength(255),
        ],
      ],
      columnType: this.formBuilder.group({
        type: '',
        value: null,
      }),
      columnConstraints: this.formBuilder.array(
        Object.keys(this.constraints).map((key) => false)
      ),
    });
  }

  private initFieldTypes() {
    this.fieldTypes$ = this.fieldTypeService.getFieldTypes();
  }

  private initConstraints() {
    this.constraintService.getConstraints().subscribe((res) => {
      this.constraints = res.result;
      this.initForm();
    });
  }

  submit(): void {
    const constraintsToSave = this.form.value.columnConstraints
      .map((v: any, i: number) => v && this.constraints[i])
      .filter((v: any) => !!v);

    const valueToSave = Object.assign({}, this.form.value, {
      columnConstraints: constraintsToSave,
    });

    this.columnService.addColumn(this.tableId, valueToSave).subscribe((res) => {
      console.log(res);
      this._location.back();
    });
  }
}
