import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductModifierHubComponent } from './product-modifier-hub.component';

describe('ProductModifierHubComponent', () => {
  let component: ProductModifierHubComponent;
  let fixture: ComponentFixture<ProductModifierHubComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProductModifierHubComponent]
    });
    fixture = TestBed.createComponent(ProductModifierHubComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
