import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog, MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { of } from 'rxjs';
import { ItemListComponent } from './item-list.component';
import { ListItem } from '../../../models/ListItem';
import { ItemAction } from '../../../models/enums/ItemAction';
import { ItemModalComponent } from '../item-modal/item-modal.component';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { NoopAnimationsModule, BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('ItemListComponent', () => {
  let component: ItemListComponent;
  let fixture: ComponentFixture<ItemListComponent>;
  let dialog: MatDialog;
  let dialogRefSpy: jest.Mocked<MatDialogRef<ItemModalComponent>>;

  const mockListItem: ListItem = {
    id: 1,
    name: 'Test Item',
    description: 'Description',
    amount: 100,
    date: '2023-01-01',
    category: 'Category',
    userId: 1
  };

  beforeEach(async () => {
    dialogRefSpy = {
      close: jest.fn(),
      afterClosed: jest.fn() 
    } as unknown as jest.Mocked<MatDialogRef<ItemModalComponent>>;
  
    await TestBed.configureTestingModule({
      declarations: [ItemListComponent],
      imports: [
        ReactiveFormsModule,
        MatSelectModule,
        MatOptionModule,
        MatFormFieldModule,
        MatInputModule,
        MatDialogModule,
        NoopAnimationsModule,
        BrowserAnimationsModule,
      ],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: null },
        { provide: MatDialogRef, useValue: dialogRefSpy },
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  
    fixture = TestBed.createComponent(ItemListComponent);
    component = fixture.componentInstance;
    dialog = TestBed.inject(MatDialog);
    component.items = [mockListItem];
    component.itemType = 'income';
    fixture.detectChanges();
  });
  
  it('should create the component', () => {
    // Then
    expect(component).toBeTruthy();
  });

  it('should emit Update action when editing an item', () => {
    // Given
    jest.spyOn(component.itemAction, 'emit');
    jest.spyOn(dialog, 'open').mockReturnValue(dialogRefSpy);
    dialogRefSpy.afterClosed.mockReturnValue(of(mockListItem));

    // When
    component.onEdit(mockListItem);

    // Then
    expect(dialog.open).toHaveBeenCalledWith(ItemModalComponent, expect.any(Object));
    expect(component.itemAction.emit).toHaveBeenCalledWith({
      item: mockListItem,
      action: ItemAction.Update
    });
  });

  it('should emit Delete action when deleting an item', () => {
    // Given
    jest.spyOn(component.itemAction, 'emit');

    // When
    component.onDelete(mockListItem);

    // Then
    expect(component.itemAction.emit).toHaveBeenCalledWith({
      item: mockListItem,
      action: ItemAction.Delete
    });
  });

  it('should open modal when adding a new item', () => {
    // Given
    jest.spyOn(dialog, 'open').mockReturnValue(dialogRefSpy);
    dialogRefSpy.afterClosed.mockReturnValue(of(null));

    // When
    component.onAddItem();

    // Then
    expect(dialog.open).toHaveBeenCalledWith(ItemModalComponent, expect.any(Object));
  });

  it('should emit Create action when modal returns new item data', () => {
    // Given
    const newItem: ListItem = { ...mockListItem, id: 2, name: 'New Item' };
    jest.spyOn(component.itemAction, 'emit');
    jest.spyOn(dialog, 'open').mockReturnValue(dialogRefSpy);
    dialogRefSpy.afterClosed.mockReturnValue(of(newItem));

    // When
    component.onAddItem();

    // Then
    expect(dialog.open).toHaveBeenCalled();
    expect(component.itemAction.emit).toHaveBeenCalledWith({
      item: newItem,
      action: ItemAction.Create
    });
  });

  it('should emit Update action when modal returns updated item data', () => {
    // Given
    const updatedItem: ListItem = { ...mockListItem, amount: 200 };
    jest.spyOn(component.itemAction, 'emit');
    jest.spyOn(dialog, 'open').mockReturnValue(dialogRefSpy);
    dialogRefSpy.afterClosed.mockReturnValue(of(updatedItem));

    // When
    component.onEdit(mockListItem);

    // Then
    expect(dialog.open).toHaveBeenCalled();
    expect(component.itemAction.emit).toHaveBeenCalledWith({
      item: updatedItem,
      action: ItemAction.Update
    });
  });

  it('should not emit any action if modal returns null', () => {
    // Given
    jest.spyOn(component.itemAction, 'emit');
    jest.spyOn(dialog, 'open').mockReturnValue(dialogRefSpy);
    dialogRefSpy.afterClosed.mockReturnValue(of(null));

    // When
    component.onAddItem();

    // Then
    expect(dialog.open).toHaveBeenCalled();
    expect(component.itemAction.emit).not.toHaveBeenCalled();
  });

  it('should display empty state message when items list is empty', () => {
    // Given
    component.items = [];
    fixture.detectChanges();

    // When
    const emptyStateElement = fixture.nativeElement.querySelector('.empty-state p');

    // Then
    expect(emptyStateElement).toBeTruthy();
    expect(emptyStateElement.textContent).toContain('Nenhum item encontrado.');
  });

  it('should display item cards when items list is not empty', () => {
    // Given
    component.items = [mockListItem];
    fixture.detectChanges();

    // When
    const itemCards = fixture.nativeElement.querySelectorAll('.item-card');

    // Then
    expect(itemCards.length).toBe(1);
    expect(itemCards[0].querySelector('mat-card-title').textContent).toContain(mockListItem.name);
    expect(itemCards[0].textContent).toContain(mockListItem.description);
    expect(itemCards[0].textContent).toContain('100.00');
  });
});
