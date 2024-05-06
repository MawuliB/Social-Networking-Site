import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { GET_USER_BY_TOKEN, UPDATE_USER, UPDATE_USER_PASSWORD } from './graphql.operations';
import { UserUpdateRequest } from '../interface/user';
import { map, Observable } from 'rxjs';



@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private apollo: Apollo) { }

  getUserByToken(token: string): Observable<any> {
    return this.apollo.query({
      query: GET_USER_BY_TOKEN,
      variables: {
        token
      }
    }).pipe(map(response => response.data));
  }

  updateUser(id: string, user: UserUpdateRequest) { 
    return this.apollo.mutate({
      mutation: UPDATE_USER,
      variables: {
        id,
        user
      }
    }).pipe(map(response => response.data));
  }

  changePassword(id: string, oldPassword: string, newPassword: string){
    return this.apollo.mutate({
      mutation: UPDATE_USER_PASSWORD,
      variables: {
        id,
        oldPassword,
        newPassword
      }
    });
}
}