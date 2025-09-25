import { CartItemInterface } from "./CartItemInterface";
import { UserInterface } from "./UserInterface";

export interface CartInterface{
    cartItemsDto: CartItemInterface[]
    clientDto: UserInterface
}