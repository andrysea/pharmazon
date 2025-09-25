import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CategoryInterface } from 'src/app/model/CategoryInterface';
import { AuthappService } from 'src/app/services/authapp.service';
import { CategoryService } from 'src/app/services/category.service';

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrl: './category.component.css'
})
export class CategoryComponent implements OnInit{
  protected category$: CategoryInterface[] = [];
  protected name: string = "";
  protected message: string = "";
  protected username: string = "";
 
  constructor(private auth: AuthappService, private route: Router, private categoryService: CategoryService){
    this.name = "";
    this.message = "";
    this.username = "";
  }
  
  async ngOnInit(): Promise<void> {
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || '';
    this.categoryService.getCategories(this.auth.getHeader())
      .subscribe({
        next: (data: Object[]) => {
          if(data != null){
            this.handleResponseCategory(data);
          }
        },
        error: (error: any) => {
          this.route.navigate(['/error'], {
            state: { status: error.status },
          });
        }
      })
  }

  insertCategory(name: string){
    const category: CategoryInterface = {
      code: '',
      name: name
    }

    const jsonObject = JSON.stringify(category)
    this.categoryService.insertCategory(this.auth.getHeader(), jsonObject)
      .subscribe({
        next: (data: any) => {
          if(data === null || data === undefined ){
            this.route.navigate(['/error'], {
              state: { status: 0 },
            });
          }
          else{
            this.name = '';
            this.message = data.message;
            setTimeout(() => {
              this.refresh();
            }, 3000)
          }
        },
        error: (error: any) => {
          if(error.status === 400){
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

  modifyCategory(category: CategoryInterface){
    const jsonObject = JSON.stringify(category)
    this.categoryService.updateCategory(this.auth.getHeader(), jsonObject)
      .subscribe({
        next: (data: any) => {
          if(data === null || data === undefined ){
            this.route.navigate(['/error'], {
              state: { status: 0 },
            });
          }
          else{
            this.message = data.message;
            setTimeout(() => {
              this.refresh();
            }, 3000)
          }
        },
        error: (error:any) => {
          if(error.status === 400){
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

  handleResponseCategory(response: any){
    this.category$ = response;
  }

  refresh(): void {
    window.location.reload();
  }

  closePopup() {
    this.message = '';
  }
}

