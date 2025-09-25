import { CreditCardInterface } from "./CreditCardInterface";

export interface ClientInterface{
    name: string; 
    surname: string;
    username: string
    number: string;
    email: string;
    taxId: string;
    password: string;
    birthDate: string;
    role: string
    creditCardsDto: CreditCardInterface[]
}