import { UserInterface } from "./UserInterface"

export interface MessageInterface{
    userDto: UserInterface
    message: string
    lastMessage: boolean
}