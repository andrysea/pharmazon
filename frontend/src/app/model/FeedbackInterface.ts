import { CartItemInterface } from "./CartItemInterface"
import { UserInterface } from "./UserInterface"

export interface FeedbackInterface{
    code: string
    cartItemDto: CartItemInterface
    clientDto: UserInterface
    description: string
}