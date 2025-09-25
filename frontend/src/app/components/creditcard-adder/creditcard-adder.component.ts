import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CreditCardInterface } from 'src/app/model/CreditCardInterface';
import { AuthappService } from 'src/app/services/authapp.service';
import { CreditCardService } from 'src/app/services/credit-card.service';

@Component({
  selector: 'app-creditcard-adder',
  templateUrl: './creditcard-adder.component.html',
  styleUrl: './creditcard-adder.component.css',
})
export class CreditcardAdderComponent implements OnInit{
  protected name: string = '';
  protected surname: string = '';
  protected username: string = '';
  protected number: string = '';
  protected cardSecurityCode: string = '';
  protected cardExpiration: string = '';
  protected message: string = '';

  constructor(private auth: AuthappService, private route: Router, private creditCardService: CreditCardService){
    this.name = '';
    this.surname = '';
    this.username = '';
    this.number = '';
    this.cardSecurityCode = '';
    this.cardExpiration = '';
    this.message = '';
  }

  async ngOnInit(): Promise<void> {
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || '';
  }

  formatCreditCardNumber(number: string) {
    if (number.length < 19) {
      this.number = number.replace(/(\d{4})(?=\d{4}$|$)/g, '$1-');
    }
  }

  validateCreditCardNumber(event: KeyboardEvent) {
    const inputChar = String.fromCharCode(event.charCode);
    const pattern = /^[0-9]*$/;
    if (!pattern.test(inputChar)) {
        event.preventDefault();
    }
  }

  addCreditCard(){
    const creditCard: CreditCardInterface = {
      name: this.name,
      surname: this.surname,
      number: this.number.replace(/-/g, ""),
      cardSecurityCode: this.cardSecurityCode,
      expirationDate: this.cardExpiration,
      balance: 0
    }
    const jsonObject = JSON.stringify(creditCard)
    
    this.creditCardService.insertCreditCard(this.auth.getHeader(), jsonObject, this.username)
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
        if((error.status === 400) && (error.error.message != undefined)){
          this.message = error.error.message;
          this.autoClosePopup();
        }
        else{
          this.route.navigate(['/error'], {
            state: { status: error.status },
          });
        }
      }
    })
  }

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

  reloadCurrentPage(){
    setTimeout(() => {
      this.route.navigate(['/personal']);
    }, 1000); 
  }
}
