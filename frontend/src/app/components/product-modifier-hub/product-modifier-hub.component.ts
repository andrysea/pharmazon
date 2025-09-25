import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CategoryInterface } from 'src/app/model/CategoryInterface';
import { ProductInterface } from 'src/app/model/ProductInterface';
import { AuthappService } from 'src/app/services/authapp.service';
import { CategoryService } from 'src/app/services/category.service';
import { ProductService } from 'src/app/services/product.service';

@Component({
  selector: 'app-product-modifier-hub',
  templateUrl: './product-modifier-hub.component.html',
  styleUrls: ['./product-modifier-hub.component.css']
})
export class ProductModifierHubComponent implements OnInit{

  protected username: string = "";
  protected disabled: boolean = false;
  protected code: string = "";
  protected oldCode: string = "";
  protected message: string = "";
  protected isProductLoaded: boolean = false;
  protected category : CategoryInterface = {
    name: '',
    code: ''
  }
  protected category$ : CategoryInterface[] = []
  protected product: ProductInterface = {
    code: '',
    name: '',
    price: 0,
    image: '',
    description: '',
    prescription: false,
    producer: '',
    activeIngredient: '',
    activeProduct: true,
    quantity: 0,
    categoryDto: {
      name: '',
      code: ''
    }
  }

  constructor(protected auth: AuthappService, private route: Router, private routerActive: ActivatedRoute, private productService: ProductService, private categoryService: CategoryService){}

  async ngOnInit(): Promise<void> {
    await this.auth.validUser();

    window.addEventListener('beforeunload', this.beforeUnloadHandler);
    this.username = this.auth.loggedUser() || "";
    this.message = "Lascia le informazioni, se non vuoi cambiarle.";
    this.autoClosePopup();

    //Get Product's Code
    this.routerActive.queryParams.subscribe(params => {
      if ('code' in params && params['code'] !== null && params['code'] !== '') {
        this.oldCode = params['code'];
        this.code = params['code'];
        this.isProductLoaded = true;

        //Get Categories
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

        //GetInformationProductFromCode
        this.productService.getProduct(this.code, this.auth.getHeader())
        .subscribe({
          next: (data: ProductInterface) => {
            if (data != null) {
              this.product = data;
              this.category = this.product.categoryDto;
            }
            else{
              this.route.navigate(['/error'], {
                state: { status: 0 },
              });
            }
          },
          error: (error: any) => {
            if(error.status === 404){
              this.message = error.error.message;
              this.disabled = true;
              
              setTimeout(() => {
                this.route.navigate(['/catalog']);
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
      else {
        this.route.navigate(['/catalog'])
      }
    })
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
              this.product.image = base64String;
              this.message = "";
          };
          reader.readAsDataURL(file);
      } else {
          this.message = "Estensione del file non consentita. Si prega di caricare un file con estensione .png, .jpg o .jpeg.";
          fileInput.value = "";
      }
    }
  }

  modifyProduct(){ 
    const product: ProductInterface = {
      code: this.product.code,
      description: this.product.description,
      image: this.product.image,
      name: this.product.name,
      prescription: this.product.prescription,
      price: this.product.price,
      producer: this.product.producer,
      activeIngredient: this.product.activeIngredient,
      activeProduct: this.product.activeProduct,
      quantity: this.product.quantity,
      categoryDto: {
        name: this.category.name,
        code: this.category.code
      }
    }

    const jsonObject = JSON.stringify(product);
    this.productService.modifyProduct(this.auth.getHeader(), jsonObject, this.oldCode)
      .subscribe({
        next: (data: any) => {
          if (data === null || data === undefined) {
            this.route.navigate(['/error'], {
              state: { status: 0 },
            });
          } 
          else {
            this.message = data.message;
            setTimeout(() => {
              this.route.navigate(['/catalog']);
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
}
