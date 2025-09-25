import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ServiceInterface } from 'src/app/model/ServiceInterface';
import { AuthappService } from 'src/app/services/authapp.service';
import { ServiceService } from 'src/app/services/service.service';

@Component({
  selector: 'app-service-show',
  templateUrl: './service-show.component.html',
  styleUrl: './service-show.component.css'
})
export class ServiceShowComponent implements OnInit{

  constructor(protected auth: AuthappService, private route: Router, private serviceService: ServiceService, private routerActive: ActivatedRoute){}

  protected username: string = "";
  protected message: string = "";
  protected service: ServiceInterface = {
    code: "",
    description: "",
    image: "",
    name: "",
    price: 0,
    dateChosen: new Date(),
    availability: 0
  }

  async ngOnInit(): Promise<void> {
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || "";

    this.routerActive.queryParams.subscribe(params => {
      if ('code' in params && params['code'] !== null && params['code'] !== '') {
          this.service.code = params['code'];

          //GetInformationServiceFromCode
          this.serviceService.getServicesByCode(this.auth.getHeader(), this.service.code)
          .subscribe({
            next: (data: any) => {
              if (data === null || data === undefined) {
                this.route.navigate(['/error'], {
                  state: { status: 0 },
                });
              }
              else{
                this.handleResponse(data);
              }
            },
            error: (error: any) => {
              if((error.status === 400) && (error.error.message != undefined)){
                this.message = error.error.message;
              }
              else{
                this.route.navigate(['/error'], {
                  state: { status: error.status },
                });
              }
          }})
      } 
      else {
          this.route.navigate(['/service']);
      }
    });
  }

  getImage(image: string): string {
    return "data:image/jpeg;base64," + image
  }

  handleResponse(response: any){
    response.dateChosen = response.dateChosen.substring(0,16);
    this.service = response;
  }

  refresh(): void {
    window.location.reload();
  }
}
