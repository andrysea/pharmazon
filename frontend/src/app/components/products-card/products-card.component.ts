import { Component, EventEmitter, Input, OnInit, Output  } from '@angular/core';
import { Router } from '@angular/router';
import { ProductInterface } from 'src/app/model/ProductInterface';
import { AuthappService } from 'src/app/services/authapp.service';

@Component({
  selector: 'app-products-card',
  templateUrl: './products-card.component.html',
  styleUrls: ['./products-card.component.css']
})
export class ProductsCardComponent implements OnInit {
  
  protected username: string = "" 
  protected image: string = ""
   
  constructor(protected auth: AuthappService) {}

  async ngOnInit(): Promise<void>{
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || "";
    this.getImage();
  }

  getImage(): string {
    return "data:image/jpeg;base64," + this.product.image
  }

  @Input()
  product: ProductInterface  = {
    code: "",
    name: "",
    price: 0,
    image: "",
    description: "",
    prescription: false,
    producer: "",
    activeIngredient: "",
    activeProduct: true,
    quantity: 0,
    categoryDto: {
      name: '',
      code: ''
    }
  };

  @Output()
  activate = new EventEmitter()
  @Output()
  show = new EventEmitter()
  @Output()
  edit = new EventEmitter()
  @Output()
  cart = new EventEmitter()
  @Output()
  remove = new EventEmitter()

  editProduct = () =>  this.edit.emit(this.product.code)
  showProduct = () =>  this.show.emit(this.product.code)
  activateProduct = () => this.activate.emit(this.product.code)
  addCart = () => this.cart.emit(this.product.code)
  removeFromCart = () => this.remove.emit(this.product.code)

}
