import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FeedbackInterface } from 'src/app/model/FeedbackInterface';
import { AuthappService } from 'src/app/services/authapp.service';
import { FeedbackService } from 'src/app/services/feedback.service';

@Component({
  selector: 'app-feedback-admin',
  templateUrl: './feedback-admin.component.html',
  styleUrls: ['./feedback-admin.component.css']
})
export class FeedbackAdminComponent implements OnInit{

  protected feedback$: FeedbackInterface[] = [];
  protected message = "";
  protected username = "";
  protected flag = false;
  constructor(private feedbackService: FeedbackService, private auth: AuthappService, private route: Router){}
  
  async ngOnInit(): Promise<void> {
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || "";
  
    this.feedbackService.getFeedback(this.auth.getHeader())
    .subscribe({
      next: (data: Object[]) => {
        if(data === null || data === undefined){
          this.message = "Nessun feedback rilasciato.";
        }
        else{
          this.handleResponseFeedback(data);
          this.flag = true;
        }
      },
      error: (error: any) => {
        this.route.navigate(['/error'], {
          state: { status: error.status },
        });
      }
    })
  }

  deleteFeedback(code: string){ 
    this.feedbackService.deleteFeedback(this.auth.getHeader(), code)
    .subscribe({
      next: (data: any) => {
        this.message =  data.message
        setTimeout(() => {
          this.refresh()
        }, 3000) 
      },
      error: (error: any) => {
        if(error.status === 404){
          this.refresh();
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
  handleResponseFeedback(response: any){
    this.feedback$ = response;
  }

  //Refresh
  refresh(): void {
    window.location.reload();
  }

  closePopup() {
    this.message = '';

    if(!this.flag){
      this.route.navigate(['/welcome']);
    }
  }
}
