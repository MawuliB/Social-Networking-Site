import { User } from "./user"

export interface ContactList {
    id: number
    user: User
    contact: User
    isAccepted: Boolean
    isBlacklisted: Boolean
}