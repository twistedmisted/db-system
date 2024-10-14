import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { authGuard } from './guards/auth.guard';
import { DbComponent } from './components/db/db.component';
import { CreateTableComponent } from './components/create-table/create-table.component';
import { TableComponent } from './components/table/table.component';
import { EditTableComponent } from './components/edit-table/edit-table.component';
import { AddColumnComponent } from './components/add-column/add-column.component';
import { CreateDbComponent } from './components/create-db/create-db.component';
import { DbListComponent } from './components/db-list/db-list.component';
import { CreateDbTokenComponent } from './components/create-db-token/create-db-token.component';
import { EditDbComponent } from './components/edit-db/edit-db.component';
import { TableContentComponent } from './components/table-content/table-content.component';
import { AddDataComponent } from './components/add-data/add-data.component';
import { EditDataComponent } from './components/edit-data/edit-data.component';
import { AddConstraintComponent } from './components/add-constraint/add-constraint.component';
import { ExecuteQueryComponent } from './components/execute-query/execute-query.component';

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
      {
        path: 'dbs/:dbId/tables/:tableId/content',
        component: TableContentComponent,
      },
      {
        path: 'dbs/:dbId/tables/:tableId/content/add',
        component: AddDataComponent,
      },
      {
        path: 'dbs/:dbId/tables/:tableId/content/edit',
        component: EditDataComponent,
      },
      {
        path: 'dbs/:dbId/tables/:tableId/addConstraint',
        component: AddConstraintComponent,
      },
      {
        path: 'dbs/:dbId/execute',
        component: ExecuteQueryComponent,
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
