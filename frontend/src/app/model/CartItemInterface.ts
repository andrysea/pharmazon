import { ProductInterface } from "./ProductInterface";

export interface CartItemInterface {
    code: string
    name: string
    price: number
    productDto: ProductInterface
    imagePrescription: string
    quantity: number
  }