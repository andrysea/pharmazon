import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddressModifierComponent } from './address-modifier.component';

describe('AddressModifierComponent', () => {
  let component: AddressModifierComponent;
  let fixture: ComponentFixture<AddressModifierComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddressModifierComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AddressModifierComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
