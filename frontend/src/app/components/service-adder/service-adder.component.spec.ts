import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceAdderComponent } from './service-adder.component';

describe('ServiceAdderComponent', () => {
  let component: ServiceAdderComponent;
  let fixture: ComponentFixture<ServiceAdderComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ServiceAdderComponent]
    });
    fixture = TestBed.createComponent(ServiceAdderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
