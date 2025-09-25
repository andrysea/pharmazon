import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CartItemInterface } from 'src/app/model/CartItemInterface';
import { FeedbackInterface } from 'src/app/model/FeedbackInterface';
import { ProductInterface } from 'src/app/model/ProductInterface';
import { AuthappService } from 'src/app/services/authapp.service';
import { CartService } from 'src/app/services/cart.service';
import { FeedbackService } from 'src/app/services/feedback.service';
import { environment } from 'src/environments/environment.development';

@Component({
  selector: 'app-feedback',
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.css']
})
export class FeedbackComponent implements OnInit{
  
  protected feedback$: FeedbackInterface[] = []
  protected cartItem$: CartItemInterface[] = []
  protected productReviewed$: ProductInterface[] = []
  protected username: string = ""
  protected descriptions: { [key: string]: string } = {};
  protected message = "";
  protected flag = false;

  constructor(private feedbackService: FeedbackService, private cartService: CartService, private auth: AuthappService, private route: Router) {}

  async ngOnInit(): Promise<void> {   
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || "";

    this.feedbackService.getFeedbackUser(this.auth.getHeader(), this.username)
      .subscribe({
        next: (data: Object[]) => {
          if((data === null || data === undefined)){
            this.message = "Nessun feedback inserito.";
          }
          else{
            this.flag = true;
            this.handleResponseFeedback(data);
          }

          this.cartService.getCartItemDeliverd(this.auth.getHeader(), this.username)
          .subscribe({
            next: (data: Object[]) => {
              if (data != null) {
                this.flag = true;
                this.handleResponseProduct(data);
              }
              else if(!this.flag){
                this.message = "Non hai effettuato ancora nessun ordine.";
              }
            },
            error: (error: any) => {
              this.route.navigate(['/error'], {
                state: { status: error.status },
              });
            }
          })
          },
        error: (error: any) => {
          this.route.navigate(['/error'], {
            state: { status: error.status },
          });
        }
      })
  }

  sendFeedback(code: string, name: string, description: string){
    if(code != "" && description != ""){
      const feedback: FeedbackInterface = {
        code: '',
        cartItemDto: {
          code: code,
          name: name,
          price: 0,
          productDto: {
            code: '',
            name: '',
            price: 0,
            image: '',
            description: '',
            prescription: false,
            producer: '',
            activeIngredient: '',
            activeProduct: false,
            quantity: 0,
            categoryDto: {
              name: '',
              code: ''
            }
          },
          imagePrescription: '',
          quantity: 0
        },
        clientDto: {
          name: '',
          surname: '',
          username: this.username,
          email: '',
          taxId: '',
          password: '',
          birthDate: '',
          role: environment.CLIENT,
          number: ''
        },
        description: description
      }

      const jsonObject = JSON.stringify(feedback);
      this.feedbackService.createFeedback(this.auth.getHeader(), jsonObject)
      .subscribe({
        next: (data: any) => {
          this.message =  data.message;

          delete this.descriptions[code];
          setTimeout(() => {
            this.refresh();
          }, 3000) 
        },
        error: (error: any) => {
          if(error.status === 400){
            this.message = error.error.message;

            setTimeout(() => {
              this.message = '';
            }, 3000) 
          }
          else if(error.status === 404){
            this.refresh();
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
      this.message = "Non hai inserito qualche valore, affinché il feedback sia valido."
    }
  }

  updateFeedback(code: string, description: string){
    const feedback: FeedbackInterface = {
      code: code,
      clientDto: {
        name: '',
        surname: '',
        username: '',
        email: '',
        taxId: '',
        password: '',
        birthDate: '',
        role: environment.CLIENT,
        number: ''
      },
      description: description,
      cartItemDto: {
        code: '',
        name: '',
        price: 0,
        productDto: {
          code: '',
          name: '',
          price: 0,
          image: '',
          description: '',
          prescription: false,
          producer: '',
          activeIngredient: '',
          activeProduct: false,
          quantity: 0,
          categoryDto: {
            name: '',
            code: ''
          }
        },
        imagePrescription: '',
        quantity: 0
      }
    }

    const jsonObject = JSON.stringify(feedback)
    this.feedbackService.updateFeedback(this.auth.getHeader(), jsonObject)
    .subscribe({
      next: (data: any) => {
        this.message =  data.message
        this.descriptions = {}
        setTimeout(() => {
          this.refresh()
        }, 3000) 
      },
      error: (error: any) => {
        if(error.status === 400){
          this.message = error.error.message
        }
        else if(error.status === 404){
          this.refresh();
        }
        else{
          this.route.navigate(['/error'], {
            state: { status: error.status },
          });
        }
      }
    })
  }

  deleteFeedback(code: string){
    this.feedbackService.deleteFeedback(this.auth.getHeader(), code)
    .subscribe({
      next: (data: any) => {
        this.message =  data.message
        this.descriptions = {}
        setTimeout(() => {
          this.refresh()
        }, 3000) 
      },
      error: (error: any) => {
        if(error.status === 404){
          this.refresh();
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
  handleResponseProduct(response: any){
      this.cartItem$ = response;
  }

  handleResponseFeedback(response: any){
    this.feedback$ = response;
  }

  //Refresh
  refresh(): void {
    window.location.reload();
  }

  closePopup() {
    this.message = '';

    if(!this.flag){
      this.route.navigate(['/welcome']);
    }
  }
}
