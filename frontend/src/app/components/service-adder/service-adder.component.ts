import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ServiceInterface } from 'src/app/model/ServiceInterface';
import { AuthappService } from 'src/app/services/authapp.service';
import { ServiceService } from 'src/app/services/service.service';

@Component({
  selector: 'app-service-adder',
  templateUrl: './service-adder.component.html',
  styleUrls: ['./service-adder.component.css']
})
export class ServiceAdderComponent implements OnInit{

  constructor(protected auth: AuthappService, private route: Router, private serviceService: ServiceService){}

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
  }

  onFileChanged(event: any) {
    const fileInput = event.target;
    const file = fileInput.files[0];
    
    if (file) {
      const allowedExtensions = ['.png', '.jpg', '.jpeg'];
      const fileName = file.name.toLowerCase();
      const extension = fileName.substring(fileName.lastIndexOf('.'));

      if (allowedExtensions.includes(extension)) {
          const reader = new FileReader();
          reader.onload = (e) => {
              var base64String = e.target?.result as string;
              base64String = base64String.split(',')[1];
              this.service.image = base64String;
              this.message = "";
          };
          reader.readAsDataURL(file);
      } else {
          this.message = "Estensione del file non consentita. Si prega di caricare un file con estensione .png, .jpg o .jpeg.";
          fileInput.value = "";
      }
    }
  }

  createService(){  
    const jsonObject = JSON.stringify(this.service); 
    console.log(this.service.dateChosen)
    this.serviceService.createService(this.auth.getHeader(), this.username, jsonObject)
    .subscribe({
      next: (data: any) => {
        if (data === null || data === undefined) {
          this.route.navigate(['/error'], {
            state: { status: 0 },
          });
        } else {
          this.message = data.message;
          this.service = {
            code: "",
            description: "",
            image: "",
            name: "",
            price: 0,
            dateChosen: new Date(),
            availability: 0
          };
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

  reloadCurrentPage(){
    setTimeout(() => {
      this.route.navigate(['/service']);
    }, 1000); 
  }
}
