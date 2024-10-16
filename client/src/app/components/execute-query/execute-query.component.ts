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
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { QueryService } from '../../service/query.service';
import { ErrorBlockComponent } from '../error-block/error-block.component';
import { MessageService } from '../../service/message.service';
import { CustomQueryService } from '../../service/customquery.service';
import { CustomQuery } from '../../models/customquery.model';

@Component({
  selector: 'app-execute-query',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    ErrorBlockComponent,
  ],
  templateUrl: './execute-query.component.html',
  styleUrl: './execute-query.component.scss',
})
export class ExecuteQueryComponent {
  form!: FormGroup;
  content!: [];
  columnsNames!: string[];

  private readonly dbId: string;
  private queryId: number = history.state.queryId;
  private query!: CustomQuery;

  constructor(
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private queryService: QueryService,
    private messageService: MessageService,
    private customQueryService: CustomQueryService,
    private router: Router
  ) {
    this.dbId = this.activatedRoute.snapshot.paramMap.get('dbId')!;
    this.form = this.formBuilder.group({
      dbId: this.dbId,
      query: ['', [Validators.required]],
    });
    if (this.queryId) {
      this.customQueryService
        .getById(this.queryId, this.dbId)
        .subscribe((res) => {
          this.query = res.result;
          this.form.addControl('id', new FormControl(this.queryId));
          this.form.controls['query'].setValue(this.query.query);
          this.showField();
          document.getElementById('save-btn')!.style.display = 'none';
        });
    }
  }

  showField() {
    document.getElementById('save-query-section')!.style.display = 'block';
    this.form.addControl('queryName', new FormControl(''));
    if (this.query) {
      (document.getElementById('query-name') as HTMLInputElement).value =
        this.query.queryName;
      (
        document.getElementById('confirm-save-btn') as HTMLButtonElement
      ).textContent = 'Update';
    }
  }

  executeQuery(): void {
    this.queryService
      .executeCustomSelect(this.form.getRawValue())
      .subscribe((res) => {
        this.columnsNames = Object.getOwnPropertyNames(res.at(0));
        this.content = res;
      });
  }

  saveQuery(): void {
    const queryNameInput = document.getElementById(
      'query-name'
    ) as HTMLInputElement;
    const queryName = queryNameInput.value;
    if (
      queryName === null ||
      queryName === undefined ||
      queryName.length === 0
    ) {
      this.messageService.openError(['Need to specify query name']);
      return;
    }

    if (!this.form.controls['query'].valid) {
      this.messageService.openError(['Need to enter query to save']);
      return;
    }

    this.form.controls['queryName'].setValue(queryName);

    if (this.query) {
      this.customQueryService
        .update(this.form.getRawValue())
        .subscribe((res) => {
          this.messageService.openSuccess(res.message);
          this.router.navigate(['/dbs/', this.dbId, 'customQueries']);
        });
    } else {
      this.customQueryService
        .saveQuery(this.form.getRawValue())
        .subscribe((res) => {
          this.messageService.openSuccess(res.message);
          this.router.navigate(['/dbs/', this.dbId, 'customQueries']);
        });
    }
  }
}
