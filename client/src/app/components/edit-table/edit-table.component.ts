import { Component, OnInit } from '@angular/core';
import { TableService } from '../../service/table.service';
import { Table } from '../../models/table.model';
import { ActivatedRoute } from '@angular/router';
import { CommonModule, Location } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

@Component({
  selector: 'app-edit-table',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './edit-table.component.html',
  styleUrl: './edit-table.component.scss',
})
export class EditTableComponent implements OnInit {
  table!: Table;
  form!: FormGroup;

  private tableId!: string;
  private dbId!: string;

  constructor(
    private tableService: TableService,
    private activatedRoute: ActivatedRoute,
    private formBilder: FormBuilder,
    private _location: Location
  ) {}

  ngOnInit(): void {
    this.tableId = this.activatedRoute.snapshot.paramMap.get('tableId')!;
    this.dbId = this.activatedRoute.snapshot.paramMap.get('dbId')!;

    this.form = this.formBilder.group({
      id: this.tableId,
      dbId: this.dbId,
      name: [
        '',
        [
          Validators.required,
          Validators.minLength(1),
          Validators.maxLength(255),
        ],
      ],
    });

    this.tableService.getTableById(this.tableId).subscribe((res) => {
      this.table = res.result;
    });
  }

  submit(): void {
    console.log(this.form.getRawValue());
    this.tableService.update(this.form.getRawValue()).subscribe((res) => {
      this._location.back();
    });
  }
}
