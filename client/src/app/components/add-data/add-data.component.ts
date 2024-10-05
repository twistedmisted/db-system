import { Component, OnInit } from '@angular/core';
import { ErrorBlockComponent } from '../error-block/error-block.component';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { QueryService } from '../../service/query.service';
import { TableService } from '../../service/table.service';
import { Column } from '../../models/column.model';
import { Constraints } from '../../models/constraints.model';
import { MessageService } from '../../service/message.service';

@Component({
  selector: 'app-add-data',
  standalone: true,
  imports: [
    ErrorBlockComponent,
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  templateUrl: './add-data.component.html',
  styleUrl: './add-data.component.scss',
})
export class AddDataComponent implements OnInit {
  form!: FormGroup;
  columns!: Column[];
  private tableId: string;
  private dbId: string;

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
    this.tableService.getTableById(this.tableId).subscribe((res) => {
      if (res.result.columns) {
        this.columns = res.result.columns.filter((c) =>
          this.allowedToInsertValue(c.constraints)
        );
        this.form = this.formBuilder.group({});
        this.columns.forEach((c) => {
          if (c.constraints && c.constraints.notNull) {
            this.form.addControl(
              c.name,
              new FormControl('', Validators.required)
            );
          } else {
            this.form.addControl(c.name, new FormControl(''));
          }
        });
      }
    });
  }

  allowedToInsertValue(c: Constraints): boolean {
    return !(c.identity || c.autoIncrement);

  }

  submit(): void {
    this.queryService
      .save(this.tableId, this.form.getRawValue())
      .subscribe((res) => {
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
