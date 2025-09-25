import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CreditCardInterface } from 'src/app/model/CreditCardInterface';
import { AuthappService } from 'src/app/services/authapp.service';
import { CreditCardService } from 'src/app/services/credit-card.service';

@Component({
  selector: 'app-creditcard-modifier',
  templateUrl: './creditcard-modifier.component.html',
  styleUrl: './creditcard-modifier.component.css',
})
export class CreditcardModifierComponent implements OnInit {
  protected username: string = '';
  protected message: string = '';
  protected isCreditCardLoaded: boolean = false;
  protected creditCard: CreditCardInterface = {
    name: '',
    surname: '',
    number: '',
    cardSecurityCode: '',
    expirationDate: '',
    balance: 0
  }
  
  constructor(
    private auth: AuthappService,
    private route: Router,
    private creditCardService: CreditCardService,
  ) {}

  async ngOnInit(): Promise<void> {
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || '';

    if(sessionStorage.getItem("creditCard") !== null){
      this.isCreditCardLoaded = true;
      this.creditCard = JSON.parse(sessionStorage.getItem("creditCard")!);
      this.message = "Lascia le informazioni, se non vuoi cambiarle.";
    }
    else{
      this.route.navigate(['/personal']);
    }
  }

  formatCreditCardNumber(number: string) {
    if (number.length < 19) {
      number = number.replace(/(\d{4})(?=\d{4}$|$)/g, '$1-');
    }
  }

  validateCreditCardNumber(event: KeyboardEvent) {
    const inputChar = String.fromCharCode(event.charCode);
    const pattern = /^[0-9]*$/;
    if (!pattern.test(inputChar)) {
        event.preventDefault();
    }
  }

  modifyCreditCard(){
    const creditCardModified: CreditCardInterface = {
      name: this.creditCard.name,
      surname: this.creditCard.surname,
      number: this.creditCard.number.replace(/-/g, ""),
      cardSecurityCode: this.creditCard.cardSecurityCode,
      expirationDate: this.creditCard.expirationDate,
      balance: 0
    }
    const jsonObject = JSON.stringify(creditCardModified)
    
    this.creditCardService.updateCreditCard(this.auth.getHeader(), jsonObject, this.username)
    .subscribe({
      next: (data: any) => {
        if (data === null || data === undefined) {
          this.route.navigate(['/error'], {
            state: { status: 0},
          });
        } else {
          this.message = data.message;
          sessionStorage.removeItem("creditCard");
          setTimeout(() => {
            this.route.navigate(['/personal'])
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

  closePopup() {
    this.message = '';
  }

  autoClosePopup() {
    setTimeout(() => {
      this.message = '';
    }, 3000); 
  }
}
