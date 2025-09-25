import {Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BookingInterface } from 'src/app/model/BookingInterface';
import { AuthappService } from 'src/app/services/authapp.service';
import { BookingService } from 'src/app/services/booking.service';

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnInit{
  protected message : string = ""
  protected username : string = ""
  protected bookingsNotAccepted$ : BookingInterface[] = [];
  protected bookingsAccepted$ : BookingInterface[] = [];

  constructor(private auth: AuthappService, private bookingService: BookingService, private route: Router){}

  async ngOnInit(): Promise<void> { 
    this.message = "";
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || "";
    this.bookingService.getBookingsNotAccepted(this.auth.getHeader())
    .subscribe({
      next: (data: Object[]) => {
        if(data != null){
          this.bookingsNotAccepted$ = this.handleResponseBooking(this.bookingsNotAccepted$, data);
        }
      },
      error: (error: any) => {
        this.route.navigate(['/error'], {
          state: { status: error.status },
        });
      }
    })

    this.bookingService.getBookingsAccepted(this.auth.getHeader())
    .subscribe({
      next: (data: Object[]) => {
        if(data != null){
          this.bookingsAccepted$ = this.handleResponseBooking(this.bookingsAccepted$, data);
        }
      },
      error: (error:any) => {
        this.route.navigate(['/error'], {
          state: { status: error.status },
        });
      }
    })
    
    this.bookingsNotAccepted$.sort((a, b) =>
    new Date(a.service.dateChosen).getTime() - new Date(b.service.dateChosen).getTime());
    
    this.bookingsAccepted$.sort((a, b) =>
    new Date(a.service.dateChosen).getTime() - new Date(b.service.dateChosen).getTime());
  }

  //Accept or reject a booking - ADMIN Function
  acceptOrRejectBooking(booking: BookingInterface, flag: boolean){
    booking.accepted = flag;
    const jsonObject = JSON.stringify(booking);

    this.bookingService.update(this.auth.getHeader(), jsonObject).subscribe({
      next: (data: any) => {
        if(data === null || data === undefined ){
          this.route.navigate(['/error'], {
            state: { status: 0},
          });
        }
        else{
          if(flag){
            this.message = data.message;
          }
          else{
            this.bookingService.deleteBooking(this.auth.getHeader(), jsonObject, this.username).subscribe({
              next: (data: any) => {
                this.message = data.message;
                setTimeout(() => {
                  this.refresh();
                }, 3000)
              },
              error: (error: any) => {
                this.route.navigate(['/error'], {
                  state: { status: error.status },
                });
              }
            })
          }
          setTimeout(() => {
            this.refresh();
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

  //Set response
  handleResponseBooking(_bookings$: BookingInterface[], response: any){
      _bookings$ = response;
      return _bookings$;
  }

  //Refresh
  refresh(): void {
    window.location.reload();
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
