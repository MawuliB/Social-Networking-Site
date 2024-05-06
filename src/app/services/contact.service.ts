import { Injectable } from '@angular/core';
import { Apollo } from 'apollo-angular';
import { GET_ALL_CONTACTS} from './graphql.operations';
import { catchError, map } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';
import { User } from '../interface/user';

interface GetAllContactsResponse {
  getAllUsers: User[];
}

@Injectable({
  providedIn: 'root'
})
export class ContactService {

  constructor(private apollo: Apollo) { }

  getAllContacts(): Observable<User[]> {
    return this.apollo.watchQuery<GetAllContactsResponse>({
      query: GET_ALL_CONTACTS
    }).valueChanges.pipe(
      map(response => response.data.getAllUsers),
      catchError(error => throwError(error))
    );
  }


}
