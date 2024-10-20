import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Column } from '../../models/column.model';
import { QueryService } from '../../service/query.service';
import { TableService } from '../../service/table.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService } from '../../service/message.service';
import { Constraints } from '../../models/constraints.model';
import { ErrorBlockComponent } from '../error-block/error-block.component';

@Component({
  selector: 'app-edit-data',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    ErrorBlockComponent,
  ],
  templateUrl: './edit-data.component.html',
  styleUrl: './edit-data.component.scss',
})
export class EditDataComponent {
  record!: any;
  form!: FormGroup;
  columns!: Column[];
  private tableId: string;
  private dbId: string;
  private pkName = history.state.pkName;
  private pkValue = history.state.pkValue;

  constructor(
    private queryService: QueryService,
    private tableService: TableService,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private messageService: MessageService,
    private router: Router
  ) {
    this.tableId = this.activatedRoute.snapshot.paramMap.get('tableId')!;
    this.dbId = this.activatedRoute.snapshot.paramMap.get('dbId')!;
  }

  ngOnInit(): void {
    this.queryService
      .getByPK(this.tableId, this.pkName, this.pkValue)
      .subscribe((record: any) => {
        this.tableService.getTableById(this.tableId).subscribe((res) => {
          if (res.result.columns) {
            this.columns = res.result.columns.filter((c) =>
              this.allowedToInsertValue(c.constraints)
            );
            this.form = this.formBuilder.group({});
            this.columns.forEach((c) => {
              let curVal = record[c.name];
              let defVal = c.defaultValue ? c.defaultValue : '';
              let val =
                curVal !== undefined || curVal !== null ? curVal : defVal;
              console.log(curVal);
              if (c.constraints && c.constraints.notNull) {
                this.form.addControl(
                  c.name,
                  new FormControl(val, Validators.required)
                );
              } else {
                this.form.addControl(c.name, new FormControl(val));
              }
            });
            this.handleFormGroupCasting(this.form);
          }
        });
      });
  }

  private handleFormGroupCasting(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach((controlName) => {
      const control = formGroup.get(controlName);
      control?.valueChanges.subscribe((value: string) => {
        const transformedValue = this.castValue(value);
        if (transformedValue !== value) {
          control.setValue(transformedValue, { emitEvent: false });
        }
      });
    });
  }

  private castValue(value: string): any {
    if (value.toLowerCase() === 'true') {
      return true;
    } else if (value.toLowerCase() === 'false') {
      return false;
    }

    // if (!isNaN(parseInt(value, 10)) && Number.isInteger(parseFloat(value))) {
    //   return parseInt(value, 10);
    // }

    // if (!isNaN(parseFloat(value))) {
    //   return parseFloat(value);
    // }

    return value;
  }

  private allowedToInsertValue(c: Constraints): boolean {
    return !(c.identity || c.autoIncrement);
  }

  submit(): void {
    const data = {
      object: this.form.getRawValue(),
      condition: {
        columnName: this.pkName,
        operator: 'EQUALS',
        value: this.pkValue,
      },
    };
    this.queryService.update(this.tableId, data).subscribe((res) => {
      this.messageService.openSuccess(res.message);
      this.router.navigate([
        'dbs',
        this.dbId,
        'tables',
        this.tableId,
        'content',
      ]);
    });
  }
}
