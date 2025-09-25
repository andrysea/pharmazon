import { MessageInterface } from "./MessageInterface"
import { UserInterface } from "./UserInterface"

export interface ChatInterface{
    code: string
    clientDto: UserInterface
    messages: MessageInterface[]
    activeChat: boolean
}