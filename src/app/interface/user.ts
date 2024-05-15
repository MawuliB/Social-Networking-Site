export interface User {
    id: number
    firstname: String
    lastname: String
    email: String
    alias: String
    profileImageUrl: String
    profileImageId: String
    enabled: Boolean
    createdAt: String
    updatedAt: String
    accountLocked: Boolean
}

export interface UserUpdateRequest {
    firstname: String
    lastname: String
    alias: String
}