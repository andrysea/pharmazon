import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ChangePasswordRequest } from 'src/app/model/ChangePasswordRequest';
import { AuthappService } from 'src/app/services/authapp.service';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.css'
})
export class ChangePasswordComponent implements OnInit{
  protected changePasswordRequest: ChangePasswordRequest  = {
    currentPassword: '',
    newPassword: '',
    confirmationPassword: ''
  }
  protected username: string = "";
  protected message: string = "";

  constructor(private auth: AuthappService, private route: Router){
    this.changePasswordRequest = {
      currentPassword: '',
      newPassword: '',
      confirmationPassword: ''
    };
  }
  
  async ngOnInit(): Promise<void> {
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || '';
  }

  updatePassword(){
    const jsonObject = JSON.stringify(this.changePasswordRequest);
    this.auth.modifyPassword(this.auth.getHeader(), jsonObject)
    .subscribe({
      next: (data: any) => {
        if (data === null || data === undefined) {
          this.route.navigate(['/error'], {
            state: { status: 0 },
          });
        } 
        else {
          this.message = data.message + "<br>Verrai indirizzato alla login.";
          setTimeout(() => {
            this.route.navigate(['/logout']);
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
