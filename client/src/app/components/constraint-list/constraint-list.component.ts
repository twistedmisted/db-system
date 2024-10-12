import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-constraint-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './constraint-list.component.html',
  styleUrl: './constraint-list.component.scss',
})
export class ConstraintListComponent {
  @Input() constraints!: string[];
  @Input() columnName!: string;
  @Input() tableId!: string;

  @Output() deleteConstraintEvent = new EventEmitter<{}>();

  deleteConstraint(constraint: string) {
    this.deleteConstraintEvent.emit({
      columnName: this.columnName,
      constraint: constraint,
    });
  }
}
