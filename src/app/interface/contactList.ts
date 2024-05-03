import { Contact } from "./contact"

export interface ContactList {
    id: number
    user: Contact
    contact: Contact
    isAccepted: Boolean
    isBlacklisted: Boolean
}