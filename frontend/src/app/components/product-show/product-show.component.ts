import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductInterface } from 'src/app/model/ProductInterface';
import { AuthappService } from 'src/app/services/authapp.service';
import { ProductService } from 'src/app/services/product.service';

@Component({
  selector: 'app-product-show',
  templateUrl: './product-show.component.html',
  styleUrls: ['./product-show.component.css']
})
export class ProductShowComponent implements OnInit{

  protected username: string = "";
  protected code: string = "";
  protected product: ProductInterface = {
    code: '',
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

  constructor(private auth: AuthappService, private productService: ProductService, private routerActive: ActivatedRoute, private route: Router){}
  
  async ngOnInit(): Promise<void> {
    await this.auth.validUser();
    this.username = this.auth.loggedUser() || '';

    //Get Product's Code
    this.routerActive.queryParams.subscribe((params) => {
      if (
        'code' in params &&
        params['code'] !== null &&
        params['code'] !== ''
      ) {
        this.code = params['code'];

        //GetInformationProductFromCode
        this.productService.getProduct(this.code, this.auth.getHeader())
        .subscribe({
          next: (data: ProductInterface) => {
            if (data != null) {
              this.product = data;
            }
            else{
              this.route.navigate(['/error'], {
                state: { status: 0 },
              });
            }
          },
          error: (error: any) => {
            this.route.navigate(['/error'], {
              state: { status: error.status },
            });
          }
        })
      } else {
        this.route.navigate(['/catalog']);
      }
    });
  }

  getImage(image: string): string {
    return "data:image/jpeg;base64," + image
  }
}
