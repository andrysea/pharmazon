import { Component, OnInit } from '@angular/core';
import { AuthappService } from 'src/app/services/authapp.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit{
  constructor(protected auth: AuthappService){}
  ngOnInit(): void{}
}
