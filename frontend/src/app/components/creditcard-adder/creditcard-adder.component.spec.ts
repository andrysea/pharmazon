import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreditcardAdderComponent } from './creditcard-adder.component';

describe('CreditcardAdderComponent', () => {
  let component: CreditcardAdderComponent;
  let fixture: ComponentFixture<CreditcardAdderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreditcardAdderComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CreditcardAdderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
