import { Component, OnInit, ViewChild } from '@angular/core';
import { SharedModule } from '../../services/shared.module';
import { CommonModule } from '@angular/common';
import { ToastComponent } from '../toast/toast.component';
import { ToastGComponent } from '../toast-g/toast-g.component';
import { environment } from '../../../environments/environment';
import { ContactService } from '../../services/contact.service';

@Component({
  selector: 'app-my-contacts',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './my-contacts.component.html',
  styleUrl: './my-contacts.component.css'
})
export class MyContactsComponent implements OnInit {
  @ViewChild(ToastComponent) toastComponent!: ToastComponent;
  @ViewChild(ToastGComponent) toastGComponent!: ToastGComponent;

  user = JSON.parse(localStorage.getItem('user') || '{}');

  contacts: any[] = [];
  errors: any;
  API_URL = environment.API_URL;
  PRODUCTION = environment.production;

  constructor(private contactService: ContactService) { }

  ngOnInit(): void {
    this.getContactsById();
  }

  getContactsById() {
    this.contactService.getAllContactByContactId(this.user.id).subscribe({
      next: (newContacts: any[]) => {
        this.contacts = newContacts;
      },
      error: (error: any) => {
        console.error(error);
        const errorMessage = error.graphQLErrors[0].message;
        this.toastComponent.openToast(errorMessage);
      },
    });
  }

  blackListContact(arg0: any) {
    this.contactService.addToBlackList(arg0).subscribe({
      next: (response: any) => {
        this.toastGComponent.openToast('Contact blacklisted successfully!');
        this.contacts = this.contacts.filter((contact) => contact.id !== arg0);
      },
      error: (error: any) => {
        console.error(error);
        const errorMessage = error.graphQLErrors[0].message;
        this.toastComponent.openToast(errorMessage);
      },
    });
    }

  removeContact(arg0: any) {
    this.contactService.removeFromContacts(arg0).subscribe({
      next: (response: any) => {
        this.toastGComponent.openToast('Contact removed successfully!');
        this.contacts = this.contacts.filter((contact) => contact.id !== arg0);
      },
      error: (error: any) => {
        console.error(error);
        const errorMessage = error.graphQLErrors[0].message;
          this.toastComponent.openToast(errorMessage);
      },
    });
    }

}
