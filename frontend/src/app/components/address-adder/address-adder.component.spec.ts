import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddressAdderComponent } from './address-adder.component';

describe('AddressAdderComponent', () => {
  let component: AddressAdderComponent;
  let fixture: ComponentFixture<AddressAdderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddressAdderComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AddressAdderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
