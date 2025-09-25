import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BookingInterface } from 'src/app/model/BookingInterface';
import { ServiceInterface } from 'src/app/model/ServiceInterface';
import { AuthappService } from 'src/app/services/authapp.service';
import { BookingService } from 'src/app/services/booking.service';
import { ServiceService } from 'src/app/services/service.service';

@Component({
  selector: 'app-service',
  templateUrl: './service.component.html',
  styleUrls: ['./service.component.css']
})
export class ServiceComponent implements OnInit{

  protected message : string = "";
  protected username: string = "";
  protected services$ : ServiceInterface[] = [];
  protected search: string = "";
  protected dataLoaded: boolean = false;

  constructor(protected auth: AuthappService, private serviceService: ServiceService, private bookingService: BookingService,  private route: Router){}

  async ngOnInit(): Promise<void> {
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || "";

    setTimeout(() => {
      this.refresh();
    }, 120000);
    
    this.serviceService.getServices(this.auth.getHeader(), this.username)
    .subscribe({
      next: (data: Object[]) => {
        if (data != null) {
          this.handleResponse(data);
        }
        else{
          this.message = "Nessun servizio disponibile.";
        }
        this.dataLoaded = true;
      },
      error: (error: any) => {
        this.route.navigate(['/error'], {
          state: { status: error.status },
        });
      },
    })
  }

  //Book a service (User)
  bookService(codeService: string): void{    
    const service: ServiceInterface = {
      code: codeService,
      name: '',
      description: '',
      image: '',
      price: 0,
      dateChosen: new Date(),
      availability: 0
    }

    const booking: BookingInterface = {
      code: '',
      service: service,
      dateTimeCreation: new Date(),
      accepted: false
    }

    const jsonObject = JSON.stringify(booking);  
    this.bookingService.insertBooking(this.auth.getHeader(), jsonObject, this.username)
    .subscribe({
      next: (data: any) => {
        if (data === null || data === undefined) {
          this.route.navigate(['/error'], {
            state: { status: 0 },
          });
        } else {
          this.message = data.message;
          setTimeout(() => {
            this.refresh();
          }, 3000)
        }
      },
      error: (error: any) => {
        if((error.status === 400) && (error.error.message != undefined)){
          this.message = error.error.message;

          setTimeout(() => {
            this.closePopup();
          }, 3000)
        }
        else{
          this.route.navigate(['/error'], {
            state: { status: error.status },
          });
        }
      }
    })
  }

  //Remove a booking (User)
  deleteBook(codeService: string): void{
    const service: ServiceInterface = {
      code: codeService,
      name: '',
      description: '',
      image: '',
      price: 0,
      dateChosen: new Date(),
      availability: 0
    }

    const booking: BookingInterface = {
      code: '',
      service: service,
      dateTimeCreation: new Date(),
      accepted: false
    }

    const jsonObject = JSON.stringify(booking);  
    this.bookingService.deleteBooking(this.auth.getHeader(), jsonObject, this.username)
    .subscribe({
      next: (data: any) => {
        if (data === null || data === undefined) {
          this.route.navigate(['/error'], {
            state: { status: 0 },
          });
        } else {
          this.message = data.message;
          setTimeout(() => {
            this.refresh();
          }, 3000)
        }
      },
      error: (error: any) => {
        if((error.status === 400 || error.status === 404) && (error.error.message != undefined)){
          this.message = error.error.message;

          setTimeout(() => {
            this.closePopup();
          }, 3000)
        }
        else{
          this.route.navigate(['/error'], {
            state: { status: error.status },
          });
        }
      }
    })
  }

  searchingSpecificService(): void{
    this.serviceService.getServicesListByName(this.auth.getHeader(), this.username, this.search)
    .subscribe({
      next: (data: Object[]) => {
        if (data != null) {
          this.handleResponse(data);
          this.message = "";
        }
        else{
          this.services$ = [];
          this.message = "Nessun servizio disponibile.";
        } 
      },
      error: (error: any) => {
        this.route.navigate(['/error'], {
          state: { status: error.status },
        });
      }
    })
  }

  deleteService(code: string): void{
    this.serviceService.deleteService(this.auth.getHeader(), code)
    .subscribe({
      next: (data: any) => {
        if (data === null || data === undefined) {
          this.route.navigate(['/error'], {
            state: { status: 0},
          });
        } 
        else {
          this.message = data.message;
          setTimeout(() => {
            this.refresh();
          }, 3000)
        }
      },
      error: (error: any) => {
        this.route.navigate(['/error'], {
          state: { status: error.status },
        });
      }
    })
  }

  //Direct to page
  editService(code: string): void{
    this.route.navigate(['/modifyService'], {queryParams: { code: code }})
  }

  //Direct to page
  showService(code: string): void{
    this.route.navigate(['/showService'], {queryParams: { code: code }})
  }

  //Set response
  handleResponse(response: any): void{
    this.services$ = response;
  }

  refresh(): void {
    window.location.reload()
  }

  closePopup() {
    this.message = '';
  }
}
