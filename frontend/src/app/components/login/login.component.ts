import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserInterface } from 'src/app/model/UserInterface';
import { AuthappService } from 'src/app/services/authapp.service';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent{
  
  protected user$: UserInterface[] = []
  protected username: string = ""
  protected password: string = ""
  protected message: string = ""
  
  constructor(private route: Router, private authApp: AuthappService){}

  authentication(){
    this.message = "";
    this.authApp.authenticate(this.username, this.password).
    subscribe({
      next: (data:any) => {
        const accessToken = data.token.access_token;
        sessionStorage.setItem("username", this.username)
        sessionStorage.setItem("Token", `${accessToken}`)
        this.route.navigate(['/welcome']);
      },
      error: (error: any)=> {
        if((error.status === 400 || error.status === 404) && (error.error.message != undefined)){
          this.message = error.error.message;
          this.autoClosePopup();
        }
        else if(error.status === 0){
          this.message = "Errore interno, riprovare più tardi."
        }

        this.authApp.clearAll();
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
