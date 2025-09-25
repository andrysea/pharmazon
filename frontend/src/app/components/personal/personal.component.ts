import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AddressInterface } from 'src/app/model/AddressInterface';
import { CreditCardInterface } from 'src/app/model/CreditCardInterface';
import { UserInterface } from 'src/app/model/UserInterface';
import { AddressService } from 'src/app/services/address.service';
import { AuthappService } from 'src/app/services/authapp.service';
import { CreditCardService } from 'src/app/services/credit-card.service';
import { UserService } from 'src/app/services/user-data.service';

@Component({
  selector: 'app-personal',
  templateUrl: './personal.component.html',
  styleUrls: ['./personal.component.css'],
})
export class PersonalComponent {
  protected username: string = '';
  protected message: string = '';
  protected creditCard$: CreditCardInterface[] = [];
  protected address$: AddressInterface[] = [];
  protected number: string = "";
  protected dataLoaded: boolean = false;
  protected user$: UserInterface[] = [];
  protected user: UserInterface = {
    name: '',
    surname: '',
    username: '',
    email: '',
    taxId: '',
    password: '',
    birthDate: '',
    role: '',
    number: ''
  };

  constructor(
    protected auth: AuthappService,
    private route: Router,
    private userService: UserService,
    private creditCardService: CreditCardService,
    private addressService: AddressService
  ) {}
  
  async ngOnInit(): Promise<void> {
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || '';

    //Get User's Information
    this.userService.getUser(this.username, this.auth.getHeader())
    .subscribe({
      next: (data: any) => {
        if (data === null || data === undefined) {
          this.route.navigate(['/error'], {
            state: { status: 0 },
          });
        } else {
          this.user = data;
        }
        this.dataLoaded = true;
      },
      error: (error: any) => {
        this.route.navigate(['/error'], {
          state: { status: error.status },
        });
      },
    });

    if(this.auth.isLoggedAdmin()){
      //Get Users
      this.userService.getUsers(this.auth.getHeader())
      .subscribe({
        next: (data: UserInterface[]) => {
          if(data != null){
            this.user$ = data;
          }
        },
        error: (error: any) => {
          this.route.navigate(['/error'], {
            state: { status: error.status },
          });
        }
      });
    }

    if(!this.auth.isLoggedAdmin()){
      //Get User's Credit Card
      this.creditCardService.getCreditCard(this.auth.getHeader(), this.username)
      .subscribe({
          next: (data: Object[]) => {
            if (data != null) {
              this.handleResponseCreditCard(data);
            }
            else{
              this.message = "Inserirsci la carta di credito o un nuovo indirizzo.";
              this.autoClosePopup();
            }
          },
          error: (error: any) => {
            this.route.navigate(['/error'], {
              state: { status: error.status },
            });
          }
      });

      //Get User's Address
      this.addressService.getAddress(this.auth.getHeader(), this.username)
      .subscribe({
          next: (data: Object[]) => {
            if (data != null) {
              this.handleResponseAddress(data);
            }
            else{
              this.message = "Inserirsci la carta di credito o un nuovo indirizzo.";
              this.autoClosePopup();
            }
          },
          error: (error: any) => {
            this.route.navigate(['/error'], {
              state: { status: error.status },
            });
          }
      });
    }
  }

  addAddress() {
    this.route.navigate(['/addAddress'])
  }

  modifyAddress(address: AddressInterface) {
    sessionStorage.setItem("address", JSON.stringify(address))
    this.route.navigate(['/modifyAddress']);
  }

  deactivateAddress(address: AddressInterface) {
    this.addressService.deactivateAddress(this.auth.getHeader(), address.code)
    .subscribe({
      next: (data: any) => {
        if (data === null || data === undefined) {
          this.route.navigate(['/error'], {
            state: { status: 0 },
          });
        } else {
          this.message = data.message;
          setTimeout(() => {
            this.refresh();
          }, 3000)
        }
      },
      error: (error: any) => {
        this.route.navigate(['/error'], {
            state: { status: error.status },
          });
      },
    });
  }

  addCreditCard() {
    this.route.navigate(['/addCreditCard'])
  }

  modifyCreditCard(creditCard: CreditCardInterface) {
    sessionStorage.setItem("creditCard", JSON.stringify(creditCard))
    this.route.navigate(['/modifyCreditCard']);
  }

  deactivateCreditCard(creditCardNumber: string) {
    this.creditCardService.deactivateCreditCard(this.auth.getHeader(), this.username, creditCardNumber)
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
        this.route.navigate(['/error'], {
          state: { status: error.status },
        });
      },
    });
  }

  modifyUserInformation() {
    this.route.navigate(['/modifyUser']);
  }

  deleteUser(username: string) {
    this.userService.deleteUser(this.auth.getHeader(), username)
    .subscribe({
      next: (data: any) => {
        if (data === null || data === undefined) {
          this.route.navigate(['/error'], {
            state: { status: 0 },
          });
        } else {
          this.message = data.message;
          if(!this.auth.isLoggedAdmin()){
            setTimeout(() => {
              this.route.navigate(['/logout']);
            }, 3000)
          }
          else{
            setTimeout(() => {
              this.refresh();
            }, 3000)
          }
        }
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
      },
    });

  }

  handleResponseCreditCard(response: any) {
    this.creditCard$ = response;
  }

  handleResponseAddress(response: any) {
    // for (let data of response) {
    //   data.code = this.crypto.decryptData(data.code);
    //   data.name = this.crypto.decryptData(data.name);
    //   data.surname = this.crypto.decryptData(data.surname);
    //   data.number = this.crypto.decryptData(data.number);
    //   data.address = this.crypto.decryptData(data.address);
    //   data.cap = this.crypto.decryptData(data.cap);
    //   data.city = this.crypto.decryptData(data.city);
    //   data.province = this.crypto.decryptData(data.province);
    // }
    this.address$ = response;
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
}
