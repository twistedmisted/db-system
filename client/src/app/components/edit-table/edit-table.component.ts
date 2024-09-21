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

  constructor(
    private tableService: TableService,
    private activatedRoute: ActivatedRoute,
    private formBilder: FormBuilder,
    private _location: Location
  ) {}

  ngOnInit(): void {
    this.form = this.formBilder.group({
      id: 0,
      name: [
        '',
        [
          Validators.required,
          Validators.minLength(1),
          Validators.maxLength(255),
        ],
      ],
    });

    this.tableService
      .getTableById(this.activatedRoute.snapshot.paramMap.get('tableId')!)
      .subscribe((res) => {
        this.table = res.result;
      });
  }

  submit(): void {
    const objectToSave = Object.assign({}, this.form.value, {
      id: this.table.id!,
    });
    this.tableService.update(objectToSave).subscribe((res) => {
      this._location.back();
    });
  }
}
