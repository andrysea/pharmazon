import { Component} from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from 'src/app/services/user-data.service';

@Component({
  selector: 'app-password-forget',
  templateUrl: './password-forget.component.html',
  styleUrls: ['./password-forget.component.css']
})
export class PasswordForgetComponent{
  
  protected email: string = ""
  protected message: string = ""

  constructor(private route: Router, private userData: UserService) {}

  sendEmail() {
    this.userData.sendEmail(this.email)
    .subscribe({
      next: (data: any) => {
        this.email = '';
        this.message = data.message;
        setTimeout(() => {
          this.route.navigate(['/login'])
        }, 3000)
      },
      error: (error: any)=> {
        if(error.status === 400 || error.status === 404){
          this.message = error.error.message
        }
        else{
          this.message = "Riprova ad inviare nuovamente la email."
        }
    }})
  }

  closePopup() {
    this.message = '';
  }
}
