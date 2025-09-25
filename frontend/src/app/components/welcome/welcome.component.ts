import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserInterface } from 'src/app/model/UserInterface';
import { AuthappService } from 'src/app/services/authapp.service';
import { UserService } from 'src/app/services/user-data.service';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit{

  protected username: string = "";
  protected user$: UserInterface[] = [];
  constructor(private route: Router, protected auth: AuthappService, private userService: UserService) {
  }

  async ngOnInit(): Promise<void>{
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || "";
    this.userService.getUser(this.username, this.auth.getHeader())
      .subscribe({
        next: (data: any) => {
        sessionStorage.setItem("role", data.role);
        },
        error: (error) => {
          this.route.navigate(['/error'], {
            state: { status: error.status },
          });
        }
      }
    )
  }
}