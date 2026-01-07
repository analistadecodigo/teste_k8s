import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ListItem } from '../../../models/ListItem';
import { ItemAction } from '../../../models/enums/ItemAction';
import { MatDialog } from '@angular/material/dialog';
import { ItemModalComponent } from '../item-modal/item-modal.component';
import { ItemType } from '../../../models/types/ItemType';

@Component({
  selector: 'app-item-list',
  templateUrl: './item-list.component.html',
  styleUrl: './item-list.component.css'
})
export class ItemListComponent {
  @Input() items!: ListItem[];
  @Input() itemType!: ItemType;
  @Output() itemAction = new EventEmitter<{ item: ListItem, action: ItemAction }>();

  constructor(private readonly dialog: MatDialog) {}

  onEdit(item: ListItem): void {
    this.openItemModal(item);
    this.itemAction.emit({ item, action: ItemAction.Update });
  }

  onDelete(item: ListItem): void {
    this.itemAction.emit({ item, action: ItemAction.Delete });
  }

  onAddItem(): void {
    this.openItemModal(null);
  }

  private openItemModal(item: ListItem | null): void {
    const dialogRef = this.dialog.open(ItemModalComponent, {
      data: { item, itemType: this.itemType },
      width: '60%',
      height: '90%',
      disableClose: true
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.itemAction.emit({ item: result, action: item ? ItemAction.Update : ItemAction.Create });
      }
    });

  }
}
