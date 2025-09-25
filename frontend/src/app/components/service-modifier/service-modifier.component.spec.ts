import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceModifierComponent } from './service-modifier.component';

describe('ServiceModifierComponent', () => {
  let component: ServiceModifierComponent;
  let fixture: ComponentFixture<ServiceModifierComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ServiceModifierComponent]
    });
    fixture = TestBed.createComponent(ServiceModifierComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
