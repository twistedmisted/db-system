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
import { FieldTypeService } from '../../service/fieldtype.service';
import { ColumnService } from '../../service/column.service';
import { BehaviorSubject, forkJoin } from 'rxjs';
import { Column } from '../../models/column.model';

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
  tableId!: string;
  columnName!: string;
  column!: Column;

  constructor(
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

    forkJoin({
      column: this.initColumn(),
    }).subscribe({
      next: (res) => {
        this.column = res.column.result;
        this.initForm();
      },
    });
  }

  private initFieldTypes() {
    this.fieldTypes$ = this.fieldTypeService.getFieldTypes();
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
        type: this.column.columnType.type,
        value: this.column.columnType.value,
      }),
    });
  }

  submit(): void {}
}
