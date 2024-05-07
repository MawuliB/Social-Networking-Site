import { Injectable } from '@angular/core';
import { Apollo } from 'apollo-angular';
import { ADD_TO_CONTACT, GET_ALL_CONTACT_BY_CONTACT_ID, GET_ALL_CONTACTS, GET_INVITATIONS, REMOVE_FROM_BLACKLIST, REMOVE_FROM_CONTACTS} from './graphql.operations';
import { catchError, map, switchMap } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';
import { User } from '../interface/user';

interface GetAllUsersResponse {
  getAllUsers: User[];
}

interface GetAllContactsResponse {
  getAllContactByContactId: {
    "id": number,
    "isAccepted": boolean,
    "isBlacklisted": boolean,
    "contact": User[]
  }[];
}

@Injectable({
  providedIn: 'root'
})
export class ContactService {

  constructor(private apollo: Apollo) { }

  getAllContacts(): Observable<User[]> {
    return this.apollo.watchQuery<GetAllUsersResponse>({
      query: GET_ALL_CONTACTS
    }).valueChanges.pipe(
      map(response => response.data.getAllUsers),
      catchError(error => throwError(error))
    );
  }

  getAllContactByContactId(contactId: string): Observable<GetAllContactsResponse[]> {
    const query = this.apollo.watchQuery<{getAllContactByContactId: GetAllContactsResponse[]}>({
      query: GET_ALL_CONTACT_BY_CONTACT_ID,
      variables: {
        contactId
      }
    });
  
    return query.valueChanges.pipe(
      switchMap(() => query.refetch()),
      map(response => response.data.getAllContactByContactId),
      catchError(error => throwError(error))
    );
  }

  removeFromBlackList(id: string){
    return this.apollo.mutate({
      mutation: REMOVE_FROM_BLACKLIST,
      variables: {
        id
      }
    });
  }

  removeFromContacts(id: string){
    return this.apollo.mutate({
      mutation: REMOVE_FROM_CONTACTS,
      variables: {
        id
      }
    });
  }

  addToContact(user: string, contact: string,){
    return this.apollo.mutate({
      mutation: ADD_TO_CONTACT,
      variables: {
        user,
        contact,
        isAccepted: false,
        isBlacklisted: false
      }
    });
  }

  getInvitations(userId: number): Observable<GetAllContactsResponse[]> {
    const query = this.apollo.watchQuery<{getInvitationsByUserId: GetAllContactsResponse[]}>({
      query: GET_INVITATIONS,
      variables: {
        userId
      }
    });
  
    return query.valueChanges.pipe(
      switchMap(() => query.refetch()),
      map(response => response.data.getInvitationsByUserId),
      catchError(error => throwError(error))
    );
  }

}
