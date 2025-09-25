import { CategoryInterface } from "./CategoryInterface"

export interface ProductInterface{
    code: string
    name: string
    price: number
    image: string
    description: string
    prescription: boolean
    producer: string
    activeIngredient: string
    activeProduct: boolean
    quantity: number
    categoryDto: CategoryInterface
}