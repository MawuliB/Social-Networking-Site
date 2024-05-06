import { gql } from "apollo-angular";

export const GET_ALL_CONTACTS = gql`
query GetAllUsers {
    getAllUsers {
        id
        firstname
        lastname
        email
        username
        profileImageUrl
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
        username
        profileImageUrl
    }
}
`;


export const UPDATE_USER_PASSWORD = gql`
mutation UpdatePassword($id: ID!, $oldPassword: String!, $newPassword: String!) {
    updatePassword(id: $id, oldPassword: $oldPassword, newPassword: $newPassword) {
        id
        firstname
        lastname
        email
        username
        profileImageUrl
    }
}
`;

export const GET_USER_BY_TOKEN = gql`
query GetUserByToken($token: String!) {
    getUserByToken(token: $token) {
        id
        firstname
        lastname
        email
        username
        profileImageUrl
    }
}
`;