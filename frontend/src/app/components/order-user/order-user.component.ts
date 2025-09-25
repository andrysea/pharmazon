import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { OrderInterface } from 'src/app/model/OrderInterface';
import { AuthappService } from 'src/app/services/authapp.service';
import { OrderService } from 'src/app/services/order.service';

@Component({
  selector: 'app-order-user',
  templateUrl: './order-user.component.html',
  styleUrls: ['./order-user.component.css']
})
export class OrderUserComponent implements OnInit{
  
  protected orders$: OrderInterface[] = [];
  protected username = "";
  protected message = "";
  protected dataLoaded: boolean = false;

  constructor(protected auth: AuthappService, private route: Router, private orderService: OrderService){}

  async ngOnInit(): Promise<void> {
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || "";

    this.orderService.getListOrderClient(this.auth.getHeader(), this.username)
    .subscribe({
      next: (data: Object[]) => {
        if(data != null){
          this.handleResponseOrder(data);
        }
        else{
          this.message = "Nessun ordine ancora effettuato.";
        }
        this.dataLoaded = true;
      },
      error: (error: any) => {
        this.route.navigate(['/error'], {
          state: { status: error.status },
        });
      }
    })
  }
  
  //Set response
  handleResponseOrder(response: any){
    this.orders$ = response;
  }
  
  closePopup() {
    this.message = '';
  }
}
