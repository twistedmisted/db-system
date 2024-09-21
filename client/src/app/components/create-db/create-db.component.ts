import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { DbService } from '../../service/db/db.service';
import { DbTokenLifeTimeService } from '../../service/dbtokenlifetime.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-db',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './create-db.component.html',
  styleUrl: './create-db.component.scss',
})
export class CreateDbComponent implements OnInit {
  form!: FormGroup;
  dbTokenLifeTimes!: string[];

  constructor(
    private dbService: DbService,
    private dbTokenLifeTimeService: DbTokenLifeTimeService,
    private formBuilder: FormBuilder,
    private router: Router
  ) {
    console.log('initing con');
  }

  ngOnInit(): void {
    console.log('initing');
    this.initDbTokenLifeTimes();
    this.initForm();
  }

  private initDbTokenLifeTimes() {
    console.log('initing tokines');
    this.dbTokenLifeTimeService.getAll().subscribe((res) => {
      this.dbTokenLifeTimes = res.result;
    });
  }

  private initForm() {
    console.log('initing forms');
    this.form = this.formBuilder.group({
      dbName: [
        '',
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(256),
        ],
      ],
      tokenLifeTime: ['', [Validators.required]],
    });
  }

  submit() {
    this.dbService.save(this.form.getRawValue()).subscribe((res) => {
      console.log(res);
      this.router.navigate(['dbs']).then(() => window.location.reload());
    });
  }
}
