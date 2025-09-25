import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserModifierComponent } from './user-modifier.component';

describe('UserModifierComponent', () => {
  let component: UserModifierComponent;
  let fixture: ComponentFixture<UserModifierComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserModifierComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UserModifierComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
