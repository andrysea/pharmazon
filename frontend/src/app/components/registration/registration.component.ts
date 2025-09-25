import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserInterface } from 'src/app/model/UserInterface';
import { AuthappService } from 'src/app/services/authapp.service';
import { environment } from 'src/environments/environment.development';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent {
  protected name: string = '';
  protected surname: string = '';
  protected username: string = '';
  protected number: string = '';
  protected email: string = '';
  protected taxId: string = '';
  protected password: string = '';
  protected birthDate: string = '';
  protected message: string = '';

  constructor(private auth: AuthappService,  private route: Router){}

  registration(){
    const user: UserInterface = {
      name: this.name,
      surname: this.surname,
      username: this.username,
      number: this.number,
      email: this.email,
      taxId: this.taxId,
      password: this.password,
      birthDate: this.birthDate,
      role: environment.CLIENT
    }

    this.auth.registration(user)
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
      this.route.navigate(['/login']);
    }, 1000); 
  }
}
