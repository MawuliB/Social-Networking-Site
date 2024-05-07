import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { GET_USER_BY_TOKEN, UPDATE_USER, UPDATE_USER_PASSWORD } from './graphql.operations';
import { User, UserUpdateRequest } from '../interface/user';
import { catchError, map, Observable, switchMap, throwError } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private apollo: Apollo) { }

  getUserByToken(token: string): Observable<any> {
    const query = this.apollo.watchQuery<{ getUserByToken: any }>({
      query: GET_USER_BY_TOKEN,
      variables: {
        token
      }
    });
  
    return query.valueChanges.pipe(
      switchMap(() => query.refetch()),
      map(response => response.data),
      catchError(error => throwError(error))
    );
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