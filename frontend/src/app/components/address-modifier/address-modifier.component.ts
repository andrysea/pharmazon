import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AddressInterface } from 'src/app/model/AddressInterface';
import { AddressService } from 'src/app/services/address.service';
import { AuthappService } from 'src/app/services/authapp.service';


@Component({
  selector: 'app-address-modifier',
  templateUrl: './address-modifier.component.html',
  styleUrl: './address-modifier.component.css'
})
export class AddressModifierComponent implements OnInit{

  protected username: string = '';
  protected message: string = '';
  protected isAddressLoaded: boolean = false;
  protected address: AddressInterface = {
    code: '',
    name: '',
    surname: '',
    number: '',
    address: '',
    cap: '',
    city: '',
    province: ''
  }

  constructor(private auth: AuthappService, private route: Router, private addressService: AddressService){}

  async ngOnInit(): Promise<void> {
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || '';

    if(sessionStorage.getItem("address") !== null){
      this.address = JSON.parse(sessionStorage.getItem("address")!);
      this.isAddressLoaded = true;
      this.message = "Lascia le informazioni, se non vuoi cambiarle.";
    }
    else{
      this.route.navigate(['/personal']);
    }
  }

  modifyAddress(address: AddressInterface){
    const addressUpdate: AddressInterface = {
      code: address.code,
      name: address.name,
      surname: address.surname,
      number: address.number,
      address: address.address,
      cap: address.cap,
      city: address.city,
      province: address.province
    }
    const jsonObject = JSON.stringify(addressUpdate);

    this.addressService.updateAddress(this.auth.getHeader(), jsonObject, this.username)
    .subscribe({
      next: (data: any) => {
        if (data === null || data === undefined) {
          this.route.navigate(['/error'], {
            state: { status: 0 },
          });
        } else {
          this.message = data.message;
          sessionStorage.removeItem("address");
          setTimeout(() => {
            this.route.navigate(['/personal']);
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

  refresh(): void {
    window.location.reload();
  }

  autoClosePopup() {
    setTimeout(() => {
      this.message = '';
    }, 3000); 
  }
}
