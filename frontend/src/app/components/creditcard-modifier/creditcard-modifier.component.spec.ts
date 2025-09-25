import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreditcardModifierComponent } from './creditcard-modifier.component';

describe('CreditcardModifierComponent', () => {
  let component: CreditcardModifierComponent;
  let fixture: ComponentFixture<CreditcardModifierComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreditcardModifierComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CreditcardModifierComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
