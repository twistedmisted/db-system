import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { authGuard } from './guards/auth.guard';
import { DbComponent } from './components/db/db.component';
import { CreateTableComponent } from './components/create-table/create-table.component';
import { TableComponent } from './components/table/table.component';
import { EditTableComponent } from './components/edit-table/edit-table.component';
import { EditColumnComponent } from './components/edit-column/edit-column.component';
import { AddColumnComponent } from './components/add-column/add-column.component';
import { CreateDbComponent } from './components/create-db/create-db.component';
import { DbListComponent } from './components/db-list/db-list.component';
import { CreateDbTokenComponent } from './components/create-db-token/create-db-token.component';
import { EditDbComponent } from './components/edit-db/edit-db.component';

export const routes: Routes = [
  {
    path: '',
    canActivate: [authGuard],
    children: [
      {
        path: '',
        component: HomeComponent,
      },
      {
        path: 'dbs',
        component: DbListComponent,
      },
      {
        path: 'dbs/:id',
        component: DbComponent,
      },
      {
        path: 'dbs/:id/edit',
        component: EditDbComponent,
      },
      {
        path: 'tables/create',
        component: CreateTableComponent,
      },
      {
        path: 'dbs/:dbId/tables/:tableId',
        component: TableComponent,
      },
      {
        path: 'dbs/:dbId/tables/:tableId/edit',
        component: EditTableComponent,
      },
      {
        path: 'dbs/:dbId/tables/:tableId/columns/new',
        component: AddColumnComponent,
      },
      // {
      //   path: 'dbs/:dbId/tables/:tableId/columns/:columnName/edit',
      //   component: EditColumnComponent,
      // },
      {
        path: 'add/db',
        component: CreateDbComponent,
      },
      {
        path: 'dbs/:dbId/token/create',
        component: CreateDbTokenComponent,
      },
    ],
  },
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'register',
    component: RegisterComponent,
  },
];
