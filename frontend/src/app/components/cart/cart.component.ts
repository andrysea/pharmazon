import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AddressInterface } from 'src/app/model/AddressInterface';
import { CartItemInterface } from 'src/app/model/CartItemInterface';
import { CreditCardInterface } from 'src/app/model/CreditCardInterface';
import { OrderInterface } from 'src/app/model/OrderInterface';
import { AddressService } from 'src/app/services/address.service';
import { AuthappService } from 'src/app/services/authapp.service';
import { CartService } from 'src/app/services/cart.service';
import { CreditCardService } from 'src/app/services/credit-card.service';
import { OrderService } from 'src/app/services/order.service';
import { environment } from 'src/environments/environment.development';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit{
  protected username: string = "";
  protected flag: boolean = false;
  protected dataLoaded: boolean = false;
  protected submitButtonEnabled: boolean = false;
  protected message: string = "";
  protected image: string = "";
  protected total: number = 0.0;
  protected cartItems$ : CartItemInterface[] = [];
  protected creditCard$: CreditCardInterface[] = [];
  protected address$: AddressInterface[] = [];
  protected selectedCard: CreditCardInterface = {
    name: '',
    surname: '',
    number: '',
    cardSecurityCode: '',
    expirationDate: '',
    balance: 0
  }
  protected selectedAddress: AddressInterface = {
    code: '',
    name: '',
    surname: '',
    number: '',
    address: '',
    cap: '',
    city: '',
    province: ''
  };

  constructor(private auth: AuthappService, private creditCard: CreditCardService, private cartService: CartService, private addressService: AddressService,  
              private orderService: OrderService, private route: Router){}
  
  async ngOnInit(): Promise<void> {  
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || "";

    //Get User's Product
    this.cartService.getProductsUser(this.auth.getHeader(), this.username)
    .subscribe({
      next: (data: Object[]) => {
        if(data != null){
          this.handleResponseCartItems(data);
          this.calculateTotal();

          //Get User's Credit Card
          this.creditCard.getCreditCard(this.auth.getHeader(), this.username)
          .subscribe({
            next: (data: Object[]) => {
              if(data != null){
                this.handleResponseCreditCard(data)
              }
              else{
                this.message = "E' necessario inserire, sia l'indirizzo che la carta di credito per continuare. Vai all'area personale.";
              }
            },
            error: (error: any) => {
              this.route.navigate(['/error'], {
                state: { status: error.status },
              });
            }
          })

          //Get User's Address
          this.addressService.getAddress(this.auth.getHeader(), this.username)
          .subscribe({
            next: (data: Object[]) => {
              if(data != null){
                this.handleResponseAddress(data)
              }
              else{
                this.message = "E' necessario inserire, sia l'indirizzo che la carta di credito per continuare. Vai all'area personale.";
              }
            },
            error: (error: any) => {
              this.route.navigate(['/error'], {
                state: { status: error.status },
              });
            }
          })
        }
        else{
          this.message = "Nessun prodotto inserito nel carrello.";
          this.flag = true;
        }
        this.dataLoaded = true;
      },
      error: (error: any) => {
        if((error.status !== 404) && (error.error.message != undefined)){
          this.route.navigate(['/error'], {
            state: { status: error.status },
          });
        }
      }
    })

  }

  calculateTotal(): void {
    for (const cartItem of this.cartItems$) {
      this.total = (this.total + cartItem.productDto.price) * cartItem.quantity
    }
  }

  onFileChanged(event: any, cartItem: any): void {
    const fileInput = event.target;
    const file = fileInput.files[0];

    if (file) {
      const allowedExtensions = ['.png', '.jpg', '.jpeg'];
      const fileName = file.name.toLowerCase();
      const extension = fileName.substring(fileName.lastIndexOf('.'));

      if (allowedExtensions.includes(extension)) {
        const reader = new FileReader();
        reader.onload = (e) => {
          const base64String = e.target?.result as string;
          const image = base64String.split(',')[1];
          cartItem.imagePrescription = image;
        };
        reader.readAsDataURL(file);
      }
      else {
        this.message = "Estensione del file non consentita. Si prega di caricare un file con estensione .png, .jpg o .jpeg.";
        fileInput.value = "";
      }
    }
  }

  //Check Order Condition - FATTO
  submit() {
    if(this.selectedAddress.code != "" && this.selectedCard.number){
      const order: OrderInterface = {
        code: '',
        total: this.total,
        clientDto: {
          name: '',
          surname: '',
          username: '',
          number: '',
          email: '',
          taxId: '',
          password: '',
          birthDate: '',
          role: environment.CLIENT,
          creditCardsDto: [
            {
              name: this.selectedCard.name,
              surname: this.selectedCard.surname,
              number: this.selectedCard.number,
              cardSecurityCode: this.selectedCard.cardSecurityCode,
              expirationDate: this.selectedCard.expirationDate,
              balance: 0
            }
          ]
        },
        stateDto: {
          state: ''
        },
        cartDto: {
          cartItemsDto: this.cartItems$,
          clientDto: {
            name: '',
            surname: '',
            username: this.username,
            number: '',
            email: '',
            taxId: '',
            password: '',
            birthDate: '',
            role: environment.CLIENT
          }
        },
        addressDto: {
          code: this.selectedAddress.code,
          name: this.selectedAddress.name,
          surname: this.selectedAddress.surname,
          number: this.selectedAddress.number,
          address: this.selectedAddress.address,
          cap: this.selectedAddress.cap,
          city: this.selectedAddress.city,
          province: this.selectedAddress.province
        }
      }

      const jsonObject = JSON.stringify(order);
      this.orderService.insertOrder(this.auth.getHeader(), this.username, jsonObject)
      .subscribe({
        next: (data: any) => {
          if(data === null || data === undefined ){
            this.route.navigate(['/error'], {
              state: { status: 0 },
            });
          }
          else{
            this.message = data.message;
            setTimeout(() => {
              this.refresh()
            }, 3000)
          }
        },
        error: (error: any) => {
          if(error.status === 400){
            this.message = error.error.message;
            setTimeout(() => {
              this.refresh()
            }, 3000)
          }
          else{
            this.route.navigate(['/error'], {
              state: { status: error.status },
            });
          }
        }
      })
    }
    else{
      this.message = "Inserire carta di credito, l'indirizzo e prescrizioni, per continuare."
      setTimeout(() => {
        this.message = "";
      }, 3000)
    }
  }

  //Remove from cart the product - USER function
  removeFromCart(codeProduct: string) {
    this.cartService.removeProduct(this.auth.getHeader(), codeProduct, this.username)
    .subscribe({
      next: (data: any) => {
        if (data === null || data === undefined) {
          this.route.navigate(['/error'], {
            state: { status: 0 },
          });
        } else {
          this.message = data.message;
          setTimeout(() => {
            this.refresh()
          }, 3000)
        }
      },
      error: (error: any) => {
        if(error.status === 400){
          this.message = error.error.message
        }
        else{
          this.route.navigate(['/error'], {
            state: { status: error.status },
          });
        }
      }
    })
  }

  //Set response
  handleResponseCartItems(response: any){
    this.cartItems$ = response;
  }

  handleResponseCreditCard(response: any){
    this.creditCard$ = response;
  }

  handleResponseAddress(response: any){
    this.address$ = response;
  }

  refresh(): void {
    window.location.reload();
  }

  closePopup() {
    this.message = '';

    if(this.flag){
      this.route.navigate(['/welcome']);
    }
  }
}
