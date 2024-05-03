import { gql } from "apollo-angular";

export const GET_ALL_CONTACTS = gql`
query GetAllUsers {
    getAllUsers {
        id
        firstname
        lastname
        email
        username
        profile_image_url
    }
}
`;