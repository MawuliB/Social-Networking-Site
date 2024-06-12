import { Injectable } from '@angular/core';
import { Apollo } from 'apollo-angular';
import { GET_USER_BY_TOKEN, UPDATE_USER, UPDATE_USER_PASSWORD } from './graphql.operations';
import { UserUpdateRequest } from '../interface/user';
import { catchError, map, Observable, switchMap, throwError } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class UserService {
  url = environment.API_URL
  token = localStorage.getItem('token');

  constructor(private apollo: Apollo, private http: HttpClient) { }

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

getConnectedUsers(id: number): Observable<any> {
  const headers = new HttpHeaders()
    .set('Authorization', `Bearer ${this.token}`);
  return this.http.get(`${this.url}/users`, { params: { id: id.toString() }, headers: headers });
}

}