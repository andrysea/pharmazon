import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AddressInterface } from 'src/app/model/AddressInterface';
import { AddressService } from 'src/app/services/address.service';
import { AuthappService } from 'src/app/services/authapp.service';

@Component({
  selector: 'app-address-adder',
  templateUrl: './address-adder.component.html',
  styleUrl: './address-adder.component.css'
})
export class AddressAdderComponent implements OnInit{
  
  protected username: string = '';
  protected name: string = '';
  protected surname: string = '';
  protected number: string = '';
  protected address: string = '';
  protected cap: string = '';
  protected city: string = '';
  protected province: string = '';
  protected message: string = '';

  constructor(private auth: AuthappService, private route: Router, private addressService: AddressService){
    this.username = '';
    this.name = '';
    this.surname = '';
    this.number = '';
    this.address = '';
    this.cap = '';
    this.city = '';
    this.province = '';
    this.message = '';
  }

  async ngOnInit(): Promise<void> {
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || '';
  }
  
  addAddress() {
    const address: AddressInterface = {
      code: '',
      name: this.name,
      surname: this.surname,
      number: this.number,
      address: this.address,
      cap: this.cap,
      city: this.city,
      province: this.province
    }
    const jsonObject = JSON.stringify(address);

    this.addressService.addAddress(this.auth.getHeader(), jsonObject, this.username)
    .subscribe({
      next: (data: any) => {
        if (data === null || data === undefined) {
          this.route.navigate(['/error'], {
            state: { status: 0 },
          });
        } 
        else {
          this.message = data.message;
          setTimeout(() => {
            this.refresh();
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
