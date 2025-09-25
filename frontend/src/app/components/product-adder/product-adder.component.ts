import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CategoryInterface } from 'src/app/model/CategoryInterface';
import { ProductInterface } from 'src/app/model/ProductInterface';
import { AuthappService } from 'src/app/services/authapp.service';
import { CategoryService } from 'src/app/services/category.service';
import { ProductService } from 'src/app/services/product.service';

@Component({
  selector: 'app-product-adder',
  templateUrl: './product-adder.component.html',
  styleUrls: ['./product-adder.component.css']
})
export class ProductAdderComponent implements OnInit{

  protected username: string = ""
  protected code: string = ""
  protected description: string = ""
  protected image: string = ""
  protected name: string = ""
  protected prescription: boolean = false
  protected price: number = 0
  protected quantity: number = 0
  protected category: string = ""
  protected producer: string = ""
  protected activeIngredient: string = ""
  protected category$ : CategoryInterface[] = []
  protected message: string = ""
  
  constructor(protected auth: AuthappService, private route: Router, private productService: ProductService, private categoryService: CategoryService){}
  
  async ngOnInit(): Promise<void> {
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || "";
    
    //Get Categories
    this.categoryService.getCategories(this.auth.getHeader())
    .subscribe({
      next: (data: any) => {
        this.handleResponseCategory(data)
      },
      error: (error) => {
        this.route.navigate(['/error'], {
          state: { status: error.status },
        });
      }
    })
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
              this.image = base64String;
              this.message = "";
          };
          reader.readAsDataURL(file);
      } else {
          this.message = "Estensione del file non consentita. Si prega di caricare un file con estensione .png, .jpg o .jpeg.";
          fileInput.value = "";
      }
    }
  }

  createProduct(){
    const product: ProductInterface = {
      code: this.code,
      description: this.description,
      image: this.image,
      name: this.name,
      prescription: this.prescription,
      price: this.price,
      producer: this.producer,
      activeIngredient: this.activeIngredient,
      activeProduct: true,
      quantity: this.quantity,
      categoryDto: {
        name: this.category,
        code: ''
      }
    }

    const jsonObject = JSON.stringify(product);
    this.productService.createProduct(this.auth.getHeader(), jsonObject, this.username)
    .subscribe({
      next: (data: any) => {
        if (data === null || data === undefined) {
          this.route.navigate(['/error'], {
            state: { status: 0 },
          });
        } else {
          this.message = data.message;

          this.username = "";
          this.code = "";
          this.description = "";
          this.image = "";
          this.name = "";
          this.prescription = false;
          this.price = 0;
          this.quantity = 0;
          this.category = "";
          this.producer = "";
          this.activeIngredient = "";
          this.category$ = [];
          
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

  //Set Response
  handleResponseCategory(response: any){
    this.category$ = response;
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
      this.route.navigate(['/catalog']);
    }, 1000); 
  }
}
