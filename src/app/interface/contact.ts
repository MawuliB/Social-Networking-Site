import { User } from "./user"

export interface Contact {
    id: number
    user: User
    contact: User
    isAccepted: Boolean
    isBlacklisted: Boolean
}

export interface ContactRequest {
    user: number
    contact: number
    isAccepted: Boolean
    isBlacklisted: Boolean
}
