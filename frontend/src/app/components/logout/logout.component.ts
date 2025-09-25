import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthappService } from 'src/app/services/authapp.service';

@Component({
  selector: 'app-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.css']
})
export class LogoutComponent implements OnInit{

  protected username: string = ""

  constructor(private authApp: AuthappService, private routerActive: ActivatedRoute, private route: Router){}
  ngOnInit(): void{
    if(this.authApp.getToken()!=""){
      this.authApp.revokeToken().subscribe({
        next: () => {},
        error: () => {}
      })
    }
    this.authApp.clearAll();
    this.route.navigate(['/login']);
  }
}