import { Component, OnInit } from '@angular/core';
import { DbtokenService } from '../../service/dbtoken.service';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DbTokenLifeTimeService } from '../../service/dbtokenlifetime.service';

@Component({
  selector: 'app-create-db-token',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './create-db-token.component.html',
  styleUrl: './create-db-token.component.scss',
})
export class CreateDbTokenComponent implements OnInit {
  form!: FormGroup;
  dbTokenLifeTimes!: string[];
  dbId!: string;

  constructor(
    private dbtokenService: DbtokenService,
    private dbTokenLifeTimeService: DbTokenLifeTimeService,
    private formBuilder: FormBuilder,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.dbId = this.activatedRoute.snapshot.paramMap.get('dbId')!;

    this.initDbTokenLifeTimes();
    this.initForm();
  }

  private initDbTokenLifeTimes() {
    this.dbTokenLifeTimeService.getAll().subscribe((res) => {
      this.dbTokenLifeTimes = res.result;
    });
  }

  private initForm() {
    this.form = this.formBuilder.group({
      dbId: this.dbId,
      lifeTime: ['', [Validators.required]],
    });
  }

  submit(): void {
    this.dbtokenService.create(this.form.getRawValue()).subscribe((res) => {
      console.log(res);
      this.router.navigate(['dbs', this.dbId]);
    });
  }
}
