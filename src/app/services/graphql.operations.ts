import { gql } from "apollo-angular";

export const GET_ALL_CONTACTS = gql`
query GetAllUsers {
    getAllUsers {
        id
        firstname
        lastname
        email
        alias
        profileImageUrl
    }
}
`;

export const GET_ALL_CONTACT_BY_CONTACT_ID = gql`
query GetAllContactByContactId($contactId: Int!) {
    getAllContactByContactId(contactId: $contactId) {
        id
        isAccepted
        isBlacklisted
        contact {
            id
            firstname
            lastname
            email
            alias
            profileImageUrl
        }
    }
}
`;

export const REMOVE_FROM_BLACKLIST = gql`
mutation RemoveFromBlackList($id: Int!) {
    removeFromBlackList(contactId: $id)
}
`;

export const REMOVE_FROM_CONTACTS = gql`
mutation RemoveFromContact($id: Int!) {
    removeFromContact(contactId: $id)
}
`;

export const ACCEPT_INVITATION = gql`
mutation AcceptContactInvitation($id: Int!) {
    acceptContactInvitation(contactId: $id)
}
`;

export const ADD_TO_BLACKLIST = gql`
mutation AddToBlackList($id: Int!) {
    addToBlackList(contactId: $id)
}
`;

export const ADD_TO_CONTACT = gql`
mutation AddContact($user: Int!, $contact: Int!, $isAccepted: Boolean!, $isBlacklisted: Boolean!) {
    addToContact(contact: { user: $user, contact: $contact, isAccepted: $isAccepted, isBlacklisted: $isBlacklisted }) {
        contact {
            id
            firstname
            lastname
            email
            alias
            profileImageUrl
        }
        isAccepted
        isBlacklisted
    }
}
`;

export const UPDATE_USER = gql`
mutation UpdateUser($id: ID!, $user: UserUpdateRequest!) {
    updateUser(id: $id, user: $user) {
        id
        firstname
        lastname
        email
        alias
        profileImageUrl
    }
}
`;


export const UPDATE_USER_PASSWORD = gql`
mutation UpdatePassword($id: ID!, $oldPassword: String!, $newPassword: String!) {
    updatePassword(id: $id, oldPassword: $oldPassword, newPassword: $newPassword)
}
`;

export const GET_USER_BY_TOKEN = gql`
query GetUserByToken($token: String!) {
    getUserByToken(token: $token) {
        id
        firstname
        lastname
        email
        alias
        profileImageUrl
    }
}
`;

export const GET_INVITATIONS = gql`
query GetInvitationsUserId($userId: Int!) {
    getInvitationsByUserId(userId: $userId) {
        id
        isAccepted
        isBlacklisted
        user {
            id
            firstname
            lastname
            email
            alias
            profileImageUrl
        }
    }
}
`;