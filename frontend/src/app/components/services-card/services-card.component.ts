import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';
import { ServiceInterface } from 'src/app/model/ServiceInterface';
import { AuthappService } from 'src/app/services/authapp.service';

@Component({
  selector: 'app-services-card',
  templateUrl: './services-card.component.html',
  styleUrls: ['./services-card.component.css']
})
export class ServicesCardComponent implements OnInit{

  protected username: string = "" 
  protected image: string = ""
   
  constructor(protected auth: AuthappService, private route: Router) {}

  ngOnInit(): void{
    this.username = this.auth.loggedUser() || "";
    this.getImage();
  }

  getImage(): string {
    return "data:image/jpeg;base64," + this.service.image
  }


  @Input()
  service: ServiceInterface  = {
    code: "",
    name: "",
    description: "",
    image: "",
    price: 0,
    dateChosen: new Date(),
    availability: 0
  }

  @Output()
  delete = new EventEmitter();
  @Output()
  edit = new EventEmitter();
  @Output()
  show = new EventEmitter();
  @Output()
  book = new EventEmitter();
  @Output()
  remove = new EventEmitter();
  
  showService = () =>  this.show.emit(this.service.code)
  editArt = () =>  this.edit.emit(this.service.code);
  deleteService = () => this.delete.emit(this.service.code);
  addBook = () => this.book.emit(this.service.code);
  deleteBook = () => this.remove.emit(this.service.code);
}
