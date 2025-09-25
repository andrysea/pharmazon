import { Component, OnInit } from '@angular/core';
import { AuthappService } from 'src/app/services/authapp.service';
import { ProductInterface } from 'src/app/model/ProductInterface';
import { ProductService } from 'src/app/services/product.service';
import { Router } from '@angular/router';
import { CategoryInterface } from 'src/app/model/CategoryInterface';
import { CategoryService } from 'src/app/services/category.service';
import { CartService } from 'src/app/services/cart.service';

@Component({
  selector: 'app-gridProducts',
  templateUrl: './gridProducts.component.html',
  styleUrls: ['./gridProducts.component.css']
})
export class GridProductsComponent implements OnInit{
  
  protected products$ : ProductInterface[] = [];
  protected category$ : CategoryInterface[] = [];
  protected message : string = ""
  protected username: string = ""
  protected search: string = ""
  protected optionCategory: string = ""
  protected dataLoaded: boolean = false;
  protected isDisabled: boolean = false; 
 
  constructor(protected auth: AuthappService, private productService: ProductService, private cartService: CartService, private categoryService: CategoryService, private route: Router){}
  
  async ngOnInit(): Promise<void>{ 
    await this.auth.validUser();  
    this.username = this.auth.loggedUser() || "";

    setTimeout(() => {
      this.refresh();
    }, 120000);
    
    this.productService.getProducts(this.username, this.auth.getHeader())
    .subscribe({
      next: (data: Object[]) => {
        if (data != null) {
          this.handleResponseProduct(data);
        }
        else{
          this.message = "Nessun prodotto disponibile.";
        }
        this.dataLoaded = true;
      },
      error: (error: any) => {
        this.route.navigate(['/error'], {
          state: { status: error.status },
        });
      }
    })

    this.categoryService.getCategories(this.auth.getHeader())
    .subscribe({
      next: (data: Object[]) => {
        this.handleResponseCategory(data)
      },
      error: (error: any) => {
        this.route.navigate(['/error'], {
          state: { status: error.status },
        });
      }
    })
  }

  //Search specific product by name
  searchingSpecificProduct() {
    console.log(this.search)
    this.productService.getProductsByName(this.search, this.auth.getHeader())
    .subscribe({
      next: (data: Object[]) => {
        if (data != null) {
          this.handleResponseProduct(data);
          this.message = "";
        }
        else{
          this.products$ = [];
          this.message = "Nessun prodotto disponibile.";
        } 
      },
      error: (error: any) => {
        this.route.navigate(['/error'], {
          state: { status: error.status },
        });
      }
    })
  }

  //Search specific product by category
  searchingByCategory() {
    this.search = "";
    this.message = "";

    if((this.optionCategory != "Tutte le categorie")){
      this.isDisabled = true;
      this.productService.getProductsByCategory(this.optionCategory, this.auth.getHeader())
      .subscribe({
        next: (data: Object[]) => {
          if (data != null) {
            this.handleResponseProduct(data);
          }
          else{
            this.products$ = [];
            this.message = "Nessun prodotto disponibile.";
          }
        },
        error: (error: any) => {
          this.route.navigate(['/error'], {
            state: { status: error.status },
          });
        }
      })
    }
    else{
      //Get Products of all categories
      this.isDisabled = false;
      this.productService.getProducts(this.username, this.auth.getHeader())
      .subscribe({
        next: (data: Object[]) => {
          this.handleResponseProduct(data);
        },
        error: (error: any) => {
          this.route.navigate(['/error'], {
            state: { status: error.status },
          });
        }
      })
    }
  }

  //Go to edit product page - ADMIN function
  editProduct(codeProduct: string){
    if((this.auth.isLogged()) && (this.auth.getToken()!=null)){
      if (codeProduct && codeProduct !== '') {
        this.route.navigate(['/modifyProduct'], {queryParams: { code: codeProduct }})
      }
      else{
        this.refresh();
      }
    }
    else{
      this.auth.clearAll();
      this.route.navigate(['/login'])
    }
  }

  //Show product details
  showProduct(codeProduct: string){
    if((this.auth.isLogged()) && (this.auth.getToken()!=null)){
      if (codeProduct && codeProduct !== '') {
        this.route.navigate(['/showProduct'], {queryParams: { code: codeProduct }})
      }
      else{
        this.refresh()
      }
    }
    else{
      this.auth.clearAll()
      this.route.navigate(['/login'])
    }
  }

  //Deactivate product details - ADMIN function
  activateProduct(codeProduct: string){
    if (codeProduct && codeProduct !== '') {
      this.productService.activateProduct(this.auth.getHeader(), codeProduct)
      .subscribe({
        next: (data: any) => {
          this.message =  data.message;
          setTimeout(() => {
            this.refresh()
          }, 3000) 
        },
        error: (error: any) => {
          this.route.navigate(['/error'], {
            state: { status: error.status },
          });
        }
      })
    }
    else{
      this.refresh();
    }
  }

  //Add to cart the product - USER function
  addCart(codeProduct: string) {
    const product: ProductInterface = {
      code: codeProduct,
      name: '',
      price: 0,
      image: '',
      description: '',
      prescription: false,
      producer: '',
      activeIngredient: '',
      activeProduct: false,
      quantity: 0,
      categoryDto: {
        name: '',
        code: ''
      }
    }

    const jsonObject = JSON.stringify(product)
    this.cartService.insertProduct(this.auth.getHeader(), jsonObject, this.username)
    .subscribe({
      next: (data: any) => {
        if (data === null || data === undefined) {
          this.route.navigate(['/error'], {
            state: { status: 0},
          });
        } else {
          this.message = data.message;
          setTimeout(() => {
            this.closePopup();
          }, 3000)
        }
      },
      error: (error: any) => {
        if((error.status === 400) && (error.error.message != undefined)){
          this.message = error.error.message;
          
          setTimeout(() => {
            this.refresh();
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

  //Remove from cart the product - USER function
  removeFromCart(codeProduct: string) {
    this.cartService.removeProduct(this.auth.getHeader(), codeProduct, this.username)
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
        if(error.status === 400){
          this.message = error.error.message;

          setTimeout(() => {
            this.refresh();
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

  //Set response
  handleResponseProduct(response: any){
    this.products$ = response;
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
