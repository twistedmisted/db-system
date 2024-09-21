import { Component, Input } from '@angular/core';
import { Db } from '../../models/db.model';
import { RouterModule } from '@angular/router';
import { DbtokenService } from '../../service/dbtoken.service';

@Component({
  selector: 'app-db-token-info',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './db-token-info.component.html',
  styleUrl: './db-token-info.component.scss',
})
export class DbTokenInfoComponent {
  @Input() db!: Db;

  ClipBoardCopy(token: any) {
    token.select();
    navigator.clipboard.writeText(token.value);
  }
}
