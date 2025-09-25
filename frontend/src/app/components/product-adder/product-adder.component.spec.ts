import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductAdderComponent } from './product-adder.component';

describe('ProductAdderComponent', () => {
  let component: ProductAdderComponent;
  let fixture: ComponentFixture<ProductAdderComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProductAdderComponent]
    });
    fixture = TestBed.createComponent(ProductAdderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
