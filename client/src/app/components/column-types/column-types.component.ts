import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { Column } from '../../models/column.model';
import { ColumnService } from '../../service/column.service';
import { MessageService } from '../../service/message.service';

@Component({
  selector: 'app-column-types',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './column-types.component.html',
  styleUrl: './column-types.component.scss',
})
export class ColumnTypesComponent implements OnInit {
  @Input() options!: string[];
  @Input() index!: number;
  @Input() column!: Column;
  @Input() tableId!: string;
  @Input() columnName!: string;

  columnTypeId!: string;
  filteredOptions: string[] = this.options;
  selectedOption: string = '';
  showDropdown: boolean = false;

  private oldVal: string | null = null;
  private optionSelected: boolean = false;

  constructor(
    private columnService: ColumnService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.columnTypeId = 'columnType_' + this.index;
    this.filteredOptions = [...this.options];
  }

  filterOptions() {
    const element: HTMLInputElement = document.getElementById(
      this.columnTypeId
    )! as HTMLInputElement;
    const searchTerm = element.value.toLowerCase();
    this.filteredOptions = this.options.filter((option) => {
      return option.toLowerCase().includes(searchTerm);
    });
  }

  selectOption(option: string) {
    const element: HTMLInputElement = document.getElementById(
      this.columnTypeId
    )! as HTMLInputElement;
    this.selectedOption = option;
    this.showDropdown = false;
    element.value = this.selectedOption;
    this.optionSelected = true;
  }

  showInput(): void {
    const element: HTMLInputElement = document.getElementById(
      this.columnTypeId
    )! as HTMLInputElement;
    element.readOnly = false;
    element.classList.remove('form-control-plaintext');
    element.classList.add('form-control');
    if (this.oldVal === null) {
      this.oldVal = element.value;
    }
  }

  onEnter(): void {
    const element: HTMLInputElement = document.getElementById(
      this.columnTypeId
    )! as HTMLInputElement;
    this.columnService
      .modifyColumnType(this.tableId, {
        columnName: this.columnName,
        columnType: element.value,
      })
      .subscribe((res) => {
        this.messageService.openSuccess(res.message);
        this.oldVal = element.value;
        element.blur();
      });
  }

  lostFocus() {
    setTimeout(() => {
      const element: HTMLInputElement = document.getElementById(
        this.columnTypeId
      )! as HTMLInputElement;

      if (!this.optionSelected) {
        this.showDropdown = false;
        element.readOnly = true;
        element.classList.remove('form-control');
        element.classList.add('form-control-plaintext');
        if (this.oldVal) {
          element.value = this.oldVal;
          this.oldVal = null;
        }
        this.selectedOption = '';
        this.hideDropdown();
      } else {
        this.optionSelected = false;
        element.focus();
      }
    }, 100);
  }

  hideDropdown() {
    setTimeout(() => {
      this.showDropdown = false;
    }, 100);
  }
}
