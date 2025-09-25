import { Component, HostListener, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserInterface } from 'src/app/model/UserInterface';
import { AuthappService } from 'src/app/services/authapp.service';

import { UserService } from 'src/app/services/user-data.service';

@Component({
  selector: 'app-user-modifier',
  templateUrl: './user-modifier.component.html',
  styleUrl: './user-modifier.component.css'
})
export class UserModifierComponent implements OnInit{

  protected username: string = '';
  protected usernameNew: string = '';
  protected message: string = '';
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
  }

  constructor(
    private auth: AuthappService, 
    private route: Router, 
    private userService: UserService
  ) {}
  
  async ngOnInit(): Promise<void> {
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || '';
    this.message = "Lascia le informazioni, se non vuoi cambiarle.";
    this.autoClosePopup();

    //Get User's Information
    this.userService.getUser(this.username, this.auth.getHeader())
    .subscribe({
      next: (data: any) => {
        if (data === null || data === undefined) {
          this.route.navigate(['/error'], {
            state: { status: 0},
          });
        } else {
          this.user = data;
        }
      },
      error: (error: any) => {
        this.route.navigate(['/error'], {
          state: { status: error.status },
        });
      },
    });
  }

  modifyUser(){
    this.usernameNew = this.user.username;
    const jsonObject = JSON.stringify(this.user);

    this.userService.modifyUser(this.auth.getHeader(), jsonObject, this.username)
    .subscribe({
      next: (data: any) => {
        if (data === null || data === undefined) {
          this.route.navigate(['/error'], {
            state: { status: 0 },
          });
        } 
        else {
          if(this.usernameNew === this.username){
            this.message = data.message;

            setTimeout(() => {
              this.route.navigate(['/personal']);
            }, 3000)
          }
          else{
            this.message = data.message + "<br>Verrai indirizzato alla login.";

            setTimeout(() => {
              this.route.navigate(['/logout']);
            }, 3000)
          }
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

  @HostListener('document:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) {
    // Blocca le freccette destra e sinistra
    if (event.keyCode === 37 || event.keyCode === 39) {
      event.preventDefault();
    }
  }
}
