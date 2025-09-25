import { ServiceInterface } from "./ServiceInterface"

export interface BookingInterface{
    code: string
    service: ServiceInterface
    dateTimeCreation: Date
    accepted: boolean
}