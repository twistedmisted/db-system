import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { FieldTypeService } from '../../service/fieldtype.service';
import { BehaviorSubject } from 'rxjs';
import { ConstraintService } from '../../service/constraint.service';
import { Constraint } from '../../models/constraint.model';
import { HttpClient } from '@angular/common/http';
import { BASE_URL } from '../../constants';
import { ConstraintsResponse } from '../../models/response/ConstraintsResponse';
import { TableService } from '../../service/table.service';
import { CreateTableRequest } from '../../models/request/CreateTableRequest';

@Component({
  selector: 'app-create-table',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './create-table.component.html',
  styleUrl: './create-table.component.scss',
})
export class CreateTableComponent implements OnInit {
  form!: FormGroup;
  fieldTypes$ = new BehaviorSubject<string[]>([]);
  constraints!: Constraint[];
  private dbId = history.state.dbId;

  constructor(
    private tableService: TableService,
    private fieldTypeService: FieldTypeService,
    private constraintService: ConstraintService,
    private router: Router,
    private formBuilder: FormBuilder,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.initFormGroup();

    this.initConstraints();
    this.initFieldTypes();

    console.log('test');
    this.http
      .get<ConstraintsResponse>(BASE_URL + '/constraints')
      .subscribe((res) => console.log(res));
    console.log('test');
  }

  private initFormGroup() {
    console.log('here');
    this.form = this.formBuilder.group({
      dbId: [0, [Validators.required]],
      name: [
        '',
        [
          Validators.required,
          Validators.minLength(1),
          Validators.maxLength(255),
        ],
      ],
      columns: this.formBuilder.array([]),
    });
  }

  private initFieldTypes() {
    this.fieldTypes$ = this.fieldTypeService.getFieldTypes();
  }

  private initConstraints() {
    this.constraintService.getConstraints().subscribe((res) => {
      this.constraints = res.result;
      this.addColumn();
    });
  }

  appendNewItem(item: Constraint): void {
    this.columns.push(
      new FormGroup({
        status: new FormControl(item.value),
        price: new FormControl(item.statusValue),
      })
    );
  }

  get columns(): FormArray {
    return this.form.get('columns') as FormArray;
  }

  addColumn(): void {
    const columnForm = this.formBuilder.group({
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
    this.columns.push(columnForm);
  }

  removeColumn(index: number): void {
    if (this.columns.length > 1) {
      this.columns.removeAt(index);
    }
  }

  submit(): void {
    const newColumns = this.form.getRawValue().columns.map((c: any) => {
      let t = c.columnConstraints
        .map((v: any, i: number) => v && this.constraints[i])
        .filter((v: any) => !!v);
      return Object.assign({}, c, {
        columnConstraints: t,
      });
    });
    const valueToSave = Object.assign({}, this.form.value, {
      columns: newColumns,
    });

    const createTableRequest: CreateTableRequest = {
      dbId: this.dbId,
      name: valueToSave.name,
      columns: valueToSave.columns,
    };

    this.tableService.save(createTableRequest).subscribe((res) => {
      this.router.navigate(['dbs', this.dbId]);
    });
  }
}
