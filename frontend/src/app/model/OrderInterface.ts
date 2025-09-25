import { AddressInterface } from "./AddressInterface";
import { CartInterface } from "./CartInterface";
import { ClientInterface } from "./ClientInterface";
import { CreditCardInterface } from "./CreditCardInterface";
import { StateInterface } from "./StateInterface";

export interface OrderInterface{
    code: string
    total: number
    cartDto: CartInterface
    clientDto: ClientInterface
    stateDto: StateInterface
    addressDto: AddressInterface
}