import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ServiceInterface } from 'src/app/model/ServiceInterface';
import { AuthappService } from 'src/app/services/authapp.service';
import { ServiceService } from 'src/app/services/service.service';

@Component({
  selector: 'app-service-modifier',
  templateUrl: './service-modifier.component.html',
  styleUrls: ['./service-modifier.component.css']
})
export class ServiceModifierComponent implements OnInit{
  constructor(protected auth: AuthappService, private route: Router, private serviceService: ServiceService, private routerActive: ActivatedRoute){}
  
  protected username: string = "";
  protected disabled: boolean = false;
  protected oldCode: string = "";
  protected message: string = "";
  protected isServiceLoaded: boolean = false;
  protected service: ServiceInterface = {
    code: '',
    name: '',
    description: '',
    image: '',
    price: 0,
    dateChosen: new Date(),
    availability: 0
  }

  async ngOnInit(): Promise<void> { 
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || "";
    window.addEventListener('beforeunload', this.beforeUnloadHandler);
    this.message = "Lascia le informazioni, se non vuoi cambiarle.";

    this.routerActive.queryParams.subscribe(params => {
      if ('code' in params && params['code'] !== null && params['code'] !== '') {
          this.oldCode = params['code'];
          this.service.code = params['code'];
          this.isServiceLoaded = true;

          //GetInformationServiceFromCode
          this.serviceService.getServicesByCode(this.auth.getHeader(), this.oldCode)
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
              if(error.status === 404){
                this.message = error.error.message;
                this.disabled = true;

                setTimeout(() => {
                  this.route.navigate(['/service']);
                }, 3000)
              }
              else{
                this.route.navigate(['/error'], {
                  state: { status: error.status },
                });
              }
          }})
      } 
      else {
          this.route.navigate(['/service'])
      }
    });
  }

  ngOnDestroy(): void {
    window.removeEventListener('beforeunload', this.beforeUnloadHandler);
  }

  beforeUnloadHandler = (event: BeforeUnloadEvent) => {
    const queryParams = { ...this.routerActive.snapshot.queryParams };
    delete queryParams['code'];
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

  modifyService(){ 
    const jsonObject = JSON.stringify(this.service); 
    this.serviceService.modifyService(this.auth.getHeader(), this.oldCode, jsonObject)
    .subscribe({
      next: (data: any) => {
        if (data === null || data === undefined) {
          this.route.navigate(['/error'], {
            state: { status: 0 },
          });
        } else {
          this.message = data.message;
          setTimeout(() => {
            this.route.navigate(['/service']);
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

  handleResponse(response: any){
    response.dateChosen = response.dateChosen.substring(0,16);
    this.service = response;
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
}
