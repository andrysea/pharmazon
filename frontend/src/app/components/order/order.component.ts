import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { OrderInterface } from 'src/app/model/OrderInterface';
import { StateInterface } from 'src/app/model/StateInterface';
import { AuthappService } from 'src/app/services/authapp.service';
import { CreditCardService } from 'src/app/services/credit-card.service';
import { OrderService } from 'src/app/services/order.service';
import { StateService } from 'src/app/services/state.service';
import { environment } from 'src/environments/environment.development';

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.css']
})
export class OrderComponent implements OnInit{
  protected states$: StateInterface[] =  [];
  protected orders$: OrderInterface[] = [];
  protected ordersHistory$: OrderInterface[] = [];
  protected ordersOtherStates$: OrderInterface[] = [];
  protected flag: boolean = false;
  protected showWaitingOrder: boolean = false;
  protected noOrdersToUpdate: boolean = false;
  protected noOrdersForHistory: boolean = false;
  protected orderSelected: OrderInterface | undefined= {
    code: '',
    total: 0,
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
      creditCardsDto: []
    },
    cartDto: {
      cartItemsDto: [],
      clientDto: {
        name: '',
        surname: '',
        username: '',
        number: '',
        email: '',
        taxId: '',
        password: '',
        birthDate: '',
        role: environment.CLIENT
      }
    },
    stateDto: {
      state: ''
    },
    addressDto: {
      code: '',
      name: '',
      surname: '',
      number: '',
      address: '',
      cap: '',
      city: '',
      province: ''
    }
  }
  protected selectedOptions: { [code: string]: boolean } = {}
  protected selectedState: { [code: string]: string } = {}
  protected isStateSelectedForOrder: { [code: string]: boolean } = {}
  protected selected: string = "";
  protected enabled: boolean = false;
  protected isStateSelected: boolean = false;
  protected username = "";
  protected message = "";
  protected dataLoaded: boolean = false;

  constructor(protected auth: AuthappService, private route: Router, private routerActive: ActivatedRoute, private orderService: OrderService, private creditCardService: CreditCardService, private stateService: StateService){}
  
  async ngOnInit(): Promise<void> {
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || "";

    if(this.orders$.length === 0 &&
      this.ordersHistory$.length === 0 &&
      this.ordersOtherStates$.length === 0){
     this.message = "Al momento non è stato effettuato nessun ordine";
    }
    
    //Get orders in waiting state
    this.orderService.getListOrderWaiting(this.auth.getHeader())
    .subscribe({
      next: (data: Object[]) => {
        if(data != null && data.length > 0){
          this.handleResponseOrder(data);
          this.message = "";
        }
        this.dataLoaded = true;
      },
      error: (error: any) => {
        this.route.navigate(['/error'], {
          state: { status: error.status },
        });
      }
    })

    //Get orders NO in waiting state or deleted
    this.orderService.getListOrderNoSomeStates(this.auth.getHeader()).subscribe({
      next: (data: Object[]) => {
        if(data != null && data.length > 0){
          this.handleResponseOrderState(data);
          this.message = "";
        }
      },
      error: (error: any) => {
        this.route.navigate(['/error'], {
          state: { status: error.status },
        });
      }
    })

    //Get history of orders delivered / deleted
    this.orderService.getListOrderHistory(this.auth.getHeader()).subscribe({
      next: (data: Object[]) => {
        if(data != null && data.length > 0){
          this.handleResponseHistory(data);
          this.message = "";
        }
      },
      error: (error: any) => {
        this.route.navigate(['/error'], {
          state: { status: error.status },
        });
      }
    })

    //Get all states
    this.stateService.getStates(this.auth.getHeader()).subscribe({
      next: (data: Object[]) => {
        if(data != null && data.length > 0){
          this.handleResponseState(data);
        }
      },
      error: (error: any) => {
        this.route.navigate(['/error'], {
          state: { status: error.status },
        });
      }
    })
  }

  orderSelectedOption(event: any): void {
    if(event.target.value != "NoOrderChoose"){
      this.selected = event.target.value;
      this.orderSelected = this.orders$.find(order => order.code === this.selected);
      this.selectedOptions = {};
      this.enabled = true;
    }
    else{
      this.enabled = false;
    }
  }

  getImage(image: string): string {
    return "data:image/jpeg;base64," + image
  }

  getProductWithPrescriptionsNumber(): number {
    return this.orderSelected?.cartDto.cartItemsDto.filter((cartItem) => cartItem.productDto.prescription).length ?? 0
  }

  confirmSelection(){
    const valueSelected = Object.values(this.selectedOptions);
      
    if (valueSelected[0].toString() === "false") {
      this.orderService.approvedOrNotOrder(this.auth.getHeader(), this.selected, false)
      .subscribe({
        next: (data: any) => {
          this.message = data.message
          setTimeout(() => {
            this.refresh();
          }, 3000) 
        },
        error: (error: any) => {
          this.route.navigate(['/error'], {
            state: { status: error.status },
          });
        }
      })
    }
    else if(valueSelected[0].toString() === "true"){
      this.orderService
        .approvedOrNotOrder(this.auth.getHeader(), this.selected, true)
        .subscribe({
          next: (data: any) => {
            this.message = data.message;
            setTimeout(() => {
              this.refresh();
            }, 3000);
          },
          error: (error: any) => {
            this.route.navigate(['/error'], {
              state: { status: error.status },
            });
          },
        });
    }
  }

  updateOrder(order: OrderInterface, flag: boolean, state: string){     
    if(state === "ELIMINATO"){
      order.stateDto.state = state;
    }

    const jsonObject = JSON.stringify(order)
    this.orderService.updateOrder(this.auth.getHeader(), jsonObject, flag)
    .subscribe({
      next: (data: any) => {
        this.message = data.message
        setTimeout(() => {
          this.refresh()
        }, 3000) 
      },
      error: (error: any) => {
        if((error.status === 400) && (error.error.message != undefined)){
          this.message = error.error.message;
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
  handleResponseOrder(response: any){
    this.orders$ = response;
  }

  handleResponseOrderState(response: any){
    this.ordersOtherStates$ = response;
  }

  handleResponseHistory(response: any){
    this.ordersHistory$ = response;
  }

  handleResponseState(response: any){
    this.states$ = response;   
    this.states$ = this.states$.filter(state => state.state !== 'IN ATTESA DI APPROVAZIONE');
  }

  //Refresh
  refresh(): void {
    window.location.reload();
  }

  closePopup() {
    this.message = '';
  }

  autoClosePopup() {
    setTimeout(() => {
      this.message = '';
    }, 3000); 
  }
}
